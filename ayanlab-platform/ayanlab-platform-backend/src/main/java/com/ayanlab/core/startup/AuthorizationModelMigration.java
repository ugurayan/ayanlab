package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.core.util.PermissionCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Idempotent startup migration to the v2 authorization model:
 * <ol>
 *   <li>4-segment permission codes become {@code app:resource:action} (merging duplicates);</li>
 *   <li>global roles ({@code appId IS NULL}) are split into one role per app;</li>
 *   <li>direct role→permission grants ({@code RolePermission}) move into a per-role policy group
 *       {@code {APP}_ROLE_{CODE}_DIRECT};</li>
 *   <li>the retired {@code RolePermission} / {@code ApplicationRole} tables are renamed to {@code *_legacy};</li>
 *   <li>unique indexes on permission code and (app, org, role code).</li>
 * </ol>
 * Runs before the seed loaders so they only ever see v2 data. Legacy tables are reached through JDBC because
 * their entities no longer exist.
 */
@Slf4j
@AuthSeedSurface
@Component
@Order(1)
@RequiredArgsConstructor
public class AuthorizationModelMigration implements CommandLineRunner {

    /** Legacy explicit codes whose resource name differs from the matrix resource. */
    static final Map<String, String> LEGACY_ALIASES = Map.ofEntries(
        Map.entry("core:admin:user:read", "core:users:read"),
        Map.entry("core:admin:user:create", "core:users:create"),
        Map.entry("core:admin:user:update", "core:users:update"),
        Map.entry("core:admin:user:delete", "core:users:delete"),
        Map.entry("core:admin:role:read", "core:roles:read"),
        Map.entry("core:admin:policy:read", "core:policies:read"),
        Map.entry("core:admin:policy:manage", "core:policies:manage"),
        Map.entry("crm:accounting:ledger:read", "crm:accounting:read"),
        Map.entry("crm:accounting:invoice:read", "crm:invoices:read"),
        Map.entry("crm:clients:client:read", "crm:clients:read"),
        Map.entry("vault:documents:file:read", "vault:documents:read"),
        Map.entry("vault:documents:file:create", "vault:documents:create"),
        Map.entry("vault:sharing:share:read", "vault:sharing:read"),
        Map.entry("staffiq:rides:ride:read", "staffiq:rides:read"),
        Map.entry("staffiq:rides:ride:create", "staffiq:rides:create"),
        Map.entry("staffiq:dispatch:route:read", "staffiq:routes:read")
    );

    static final String ROLE_PERMISSION_TABLE = "RolePermission";
    static final String APPLICATION_ROLE_TABLE = "ApplicationRole";
    private static final String SCHEMA = "core";

    private final TransactionTemplate tx;
    private final JdbcTemplate jdbc;
    private final AppRepository appRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final PolicyGroupPermissionRepository policyGroupPermissionRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final RoleRepository roleRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final RolePolicyGroupRepository rolePolicyGroupRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final UserProfileRepository profileRepository;
    private final PolicyGroupRepository policyGroupRepository;
    private final MenuRepository menuRepository;

    @Override
    public void run(String... args) {
        int[] permissionStats = Objects.requireNonNull(tx.execute(status -> migratePermissionCodes()));
        int[] roleStats = Objects.requireNonNull(tx.execute(status -> splitGlobalRoles()));
        int directGroups = Objects.requireNonNull(tx.execute(status -> convertRolePermissions()));
        int retired = retireLegacyTables();
        ensureUniqueIndexes();
        int changes = permissionStats[0] + permissionStats[1] + roleStats[0] + roleStats[1] + directGroups + retired;
        if (changes > 0) {
            jdbc.update("UPDATE " + table("App") + " SET \"authzVersion\" = COALESCE(\"authzVersion\", 0) + 1");
        }
        log.info("[AuthorizationModelMigration] permissions renamed={} merged={} unconvertible={}; roles assigned={} cloned={} orphaned={}; "
                + "directPolicyGroupLinks={} legacyTablesRetired={}",
            permissionStats[0], permissionStats[1], permissionStats[2], roleStats[0], roleStats[1], roleStats[2], directGroups, retired);
    }

    // ---- permissions -------------------------------------------------------------------------------

    private int[] migratePermissionCodes() {
        int renamed = 0, merged = 0, unconvertible = 0;
        Map<String, Permission> byCode = new HashMap<>();
        List<Permission> all = permissionRepository.findAll();
        all.forEach(p -> byCode.putIfAbsent(p.getCode(), p));

        for (Permission permission : all) {
            String code = permission.getCode();
            if (code == null || PermissionCodes.isValidGrant(code)) {
                continue;
            }
            String target = LEGACY_ALIASES.getOrDefault(code, PermissionCodes.fromLegacy(code).orElse(null));
            if (target == null || !PermissionCodes.isValidGrant(target)) {
                unconvertible++;
                continue;
            }
            Permission existing = byCode.get(target);
            if (existing != null && !existing.getId().equals(permission.getId())) {
                mergeInto(permission, existing);
                byCode.remove(code);
                merged++;
            } else {
                String[] parts = target.split(":");
                permission.setCode(target);
                permission.setAppCode(parts[0]);
                permission.setResource(parts[1]);
                permission.setAction(parts[2]);
                permissionRepository.save(permission);
                byCode.remove(code);
                byCode.put(target, permission);
                renamed++;
            }
        }
        return new int[]{renamed, merged, unconvertible};
    }

    private void mergeInto(Permission legacy, Permission target) {
        UUID from = legacy.getId();
        UUID to = target.getId();
        for (ApplicationPermission link : applicationPermissionRepository.findByPermissionId(from)) {
            if (applicationPermissionRepository.existsByAppIdAndPermissionId(link.getAppId(), to)) {
                applicationPermissionRepository.delete(link);
            } else {
                link.setPermissionId(to);
                applicationPermissionRepository.save(link);
            }
        }
        if (legacyTableExists(ROLE_PERMISSION_TABLE)) {
            String rp = table(ROLE_PERMISSION_TABLE);
            jdbc.update("DELETE FROM " + rp + " a WHERE a.\"permissionId\" = ? AND EXISTS (SELECT 1 FROM " + rp
                + " b WHERE b.\"roleId\" = a.\"roleId\" AND b.\"permissionId\" = ?)", from, to);
            jdbc.update("UPDATE " + rp + " SET \"permissionId\" = ? WHERE \"permissionId\" = ?", to, from);
        }
        for (PolicyGroupPermission link : policyGroupPermissionRepository.findByPermissionId(from)) {
            if (policyGroupPermissionRepository.existsByPolicyGroupIdAndPermissionId(link.getPolicyGroupId(), to)) {
                policyGroupPermissionRepository.delete(link);
            } else {
                link.setPermissionId(to);
                policyGroupPermissionRepository.save(link);
            }
        }
        for (ProfilePermission link : profilePermissionRepository.findByPermissionId(from)) {
            if (profilePermissionRepository.existsByProfileIdAndPermissionId(link.getProfileId(), to)) {
                profilePermissionRepository.delete(link);
            } else {
                link.setPermissionId(to);
                profilePermissionRepository.save(link);
            }
        }
        permissionRepository.delete(legacy);
    }

    // ---- roles -------------------------------------------------------------------------------------

    private int[] splitGlobalRoles() {
        List<Role> globalRoles = roleRepository.findByAppIdIsNull();
        if (globalRoles.isEmpty()) {
            return new int[]{0, 0, 0};
        }
        Map<UUID, App> apps = appRepository.findAll().stream().collect(Collectors.toMap(App::getId, Function.identity()));
        Map<UUID, UUID> appByProfile = new HashMap<>();
        Map<UUID, UUID> appByPolicyGroup = new HashMap<>();
        policyGroupRepository.findAll().forEach(g -> appByPolicyGroup.put(g.getId(), g.getAppId()));
        Map<UUID, UUID> appByMenu = new HashMap<>();
        menuRepository.findAll().forEach(m -> appByMenu.put(m.getId(), m.getAppId()));
        boolean hasApplicationRoles = legacyTableExists(APPLICATION_ROLE_TABLE);

        int assigned = 0, cloned = 0, orphaned = 0;
        for (Role role : globalRoles) {
            List<ProfileRole> holders = profileRoleRepository.findByRoleId(role.getId());
            LinkedHashSet<UUID> appIds = new LinkedHashSet<>();
            if (hasApplicationRoles) {
                jdbc.queryForList("SELECT \"appId\" FROM " + table(APPLICATION_ROLE_TABLE) + " WHERE \"roleId\" = ?", UUID.class, role.getId())
                    .stream().filter(apps::containsKey).forEach(appIds::add);
            }
            for (ProfileRole holder : holders) {
                UUID appId = appByProfile.computeIfAbsent(holder.getProfileId(),
                    id -> profileRepository.findById(id).map(UserProfile::getAppId).orElse(null));
                if (appId != null && apps.containsKey(appId)) {
                    appIds.add(appId);
                }
            }
            if (appIds.isEmpty()) {
                orphaned++;
                continue;
            }

            Iterator<UUID> it = appIds.iterator();
            UUID primaryApp = it.next();
            role.setAppId(primaryApp);
            roleRepository.save(role);
            assigned++;

            while (it.hasNext()) {
                UUID appId = it.next();
                Role target = roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(appId, role.getCode())
                    .orElseGet(() -> {
                        Role copy = Role.builder()
                            .appId(appId)
                            .code(role.getCode())
                            .name(role.getName())
                            .description(role.getDescription())
                            .build();
                        copy.setIsActive(role.getIsActive());
                        IdGenerator.assignIfAbsent(copy);
                        return roleRepository.save(copy);
                    });
                cloned++;
                moveAppLinks(role, target, appId, holders, appByProfile, appByPolicyGroup, appByMenu);
                attachDirectPermissions(target, apps.get(appId), legacyRolePermissionIds(role.getId()));
            }
        }
        return new int[]{assigned, cloned, orphaned};
    }

    private void moveAppLinks(Role source, Role target, UUID appId, List<ProfileRole> holders,
                              Map<UUID, UUID> appByProfile, Map<UUID, UUID> appByPolicyGroup, Map<UUID, UUID> appByMenu) {
        for (ProfileRole holder : holders) {
            if (!appId.equals(appByProfile.get(holder.getProfileId()))) {
                continue;
            }
            if (profileRoleRepository.existsByProfileIdAndRoleId(holder.getProfileId(), target.getId())) {
                profileRoleRepository.delete(holder);
            } else {
                holder.setRoleId(target.getId());
                profileRoleRepository.save(holder);
            }
        }

        for (RolePolicyGroup link : rolePolicyGroupRepository.findByRoleId(source.getId())) {
            if (!appId.equals(appByPolicyGroup.get(link.getPolicyGroupId()))) {
                continue;
            }
            if (rolePolicyGroupRepository.existsByRoleIdAndPolicyGroupId(target.getId(), link.getPolicyGroupId())) {
                rolePolicyGroupRepository.delete(link);
            } else {
                link.setRoleId(target.getId());
                rolePolicyGroupRepository.save(link);
            }
        }

        for (RoleMenu link : roleMenuRepository.findByRoleId(source.getId())) {
            if (!appId.equals(appByMenu.get(link.getMenuId()))) {
                continue;
            }
            if (roleMenuRepository.existsByRoleIdAndMenuId(target.getId(), link.getMenuId())) {
                roleMenuRepository.delete(link);
            } else {
                link.setRoleId(target.getId());
                roleMenuRepository.save(link);
            }
        }
    }

    // ---- RolePermission → policy groups -----------------------------------------------------------

    private int convertRolePermissions() {
        if (!legacyTableExists(ROLE_PERMISSION_TABLE)) {
            return 0;
        }
        Map<UUID, List<UUID>> byRole = new LinkedHashMap<>();
        jdbc.query("SELECT \"roleId\", \"permissionId\" FROM " + table(ROLE_PERMISSION_TABLE) + " WHERE COALESCE(\"isActive\", true)",
            rs -> {
                byRole.computeIfAbsent(rs.getObject(1, UUID.class), k -> new ArrayList<>()).add(rs.getObject(2, UUID.class));
            });
        Map<UUID, App> apps = appRepository.findAll().stream().collect(Collectors.toMap(App::getId, Function.identity()));
        int links = 0;
        for (Map.Entry<UUID, List<UUID>> entry : byRole.entrySet()) {
            Optional<Role> role = roleRepository.findById(entry.getKey());
            if (role.isEmpty() || role.get().getAppId() == null || !apps.containsKey(role.get().getAppId())) {
                continue;
            }
            links += attachDirectPermissions(role.get(), apps.get(role.get().getAppId()), entry.getValue());
        }
        return links;
    }

    /** Grants {@code permissionIds} (restricted to the role's app) through the role's {@code _DIRECT} policy group. */
    private int attachDirectPermissions(Role role, App app, Collection<UUID> permissionIds) {
        Set<UUID> appPermissionIds = applicationPermissionRepository.findByAppId(app.getId()).stream()
            .map(ApplicationPermission::getPermissionId)
            .collect(Collectors.toSet());
        List<UUID> owned = permissionRepository.findAllById(new LinkedHashSet<>(permissionIds)).stream()
            .filter(p -> appPermissionIds.contains(p.getId()) || app.getCode().equals(p.getAppCode()))
            .map(Permission::getId)
            .toList();
        if (owned.isEmpty()) {
            return 0;
        }
        String code = app.getCode().toUpperCase(Locale.ROOT) + "_ROLE_" + role.getCode()
            + (role.getOrganizationId() != null ? "_" + role.getId().toString().substring(0, 8).toUpperCase(Locale.ROOT) : "")
            + "_DIRECT";
        PolicyGroup group = policyGroupRepository.findByAppIdAndCode(app.getId(), code).orElseGet(() -> {
            PolicyGroup created = PolicyGroup.builder()
                .appId(app.getId())
                .code(code)
                .name(role.getName() + " (direct permissions)")
                .description("Migrated from RolePermission for role " + role.getCode())
                .build();
            IdGenerator.assignIfAbsent(created);
            return policyGroupRepository.save(created);
        });
        if (!rolePolicyGroupRepository.existsByRoleIdAndPolicyGroupId(role.getId(), group.getId())) {
            RolePolicyGroup link = RolePolicyGroup.builder().roleId(role.getId()).policyGroupId(group.getId()).build();
            IdGenerator.assignIfAbsent(link);
            rolePolicyGroupRepository.save(link);
        }
        int added = 0;
        for (UUID permissionId : owned) {
            if (!policyGroupPermissionRepository.existsByPolicyGroupIdAndPermissionId(group.getId(), permissionId)) {
                PolicyGroupPermission link = PolicyGroupPermission.builder().policyGroupId(group.getId()).permissionId(permissionId).build();
                IdGenerator.assignIfAbsent(link);
                policyGroupPermissionRepository.save(link);
                added++;
            }
        }
        return added;
    }

    private List<UUID> legacyRolePermissionIds(UUID roleId) {
        if (!legacyTableExists(ROLE_PERMISSION_TABLE)) {
            return List.of();
        }
        return jdbc.queryForList("SELECT \"permissionId\" FROM " + table(ROLE_PERMISSION_TABLE)
            + " WHERE \"roleId\" = ? AND COALESCE(\"isActive\", true)", UUID.class, roleId);
    }

    private int retireLegacyTables() {
        int retired = 0;
        for (String name : List.of(ROLE_PERMISSION_TABLE, APPLICATION_ROLE_TABLE)) {
            if (!legacyTableExists(name)) {
                continue;
            }
            if (legacyTableExists(name + "_legacy")) {
                log.warn("[AuthorizationModelMigration] {} and {}_legacy both exist; leaving {} in place", name, name, name);
                continue;
            }
            jdbc.execute("ALTER TABLE " + table(name) + " RENAME TO \"" + name + "_legacy\"");
            retired++;
        }
        return retired;
    }

    // ---- constraints -------------------------------------------------------------------------------

    private void ensureUniqueIndexes() {
        createIndex("CREATE UNIQUE INDEX IF NOT EXISTS uq_permission_code ON " + table("Permission") + " (code)");
        createIndex("CREATE UNIQUE INDEX IF NOT EXISTS uq_role_app_org_code ON " + table("Role") + " "
            + "(\"appId\", COALESCE(\"organizationId\", '00000000-0000-0000-0000-000000000000'::uuid), code) "
            + "WHERE \"appId\" IS NOT NULL");
    }

    private void createIndex(String sql) {
        try {
            jdbc.execute(sql);
        } catch (Exception e) {
            log.warn("[AuthorizationModelMigration] Could not create index ({}): {}", sql, e.getMessage());
        }
    }

    private boolean legacyTableExists(String name) {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT to_regclass(?) IS NOT NULL", Boolean.class, table(name)));
    }

    private static String table(String name) {
        return SCHEMA + ".\"" + name + "\"";
    }
}
