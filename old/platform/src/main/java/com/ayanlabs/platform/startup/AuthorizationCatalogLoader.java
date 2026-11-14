package com.ayanlabs.platform.startup;

import com.ayanlabs.core.data.enums.PermissionEffectEnum;
import com.ayanlabs.core.data.enums.RegistrationStatusEnum;
import com.ayanlabs.core.entity.*;
import com.ayanlabs.core.repository.*;
import com.ayanlabs.core.util.IdGenerator;
import com.ayanlabs.core.util.PermissionCodes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Idempotent loader for permission-catalog.json and authorization-catalog.json.
 * Seeds explicit permissions, permission matrices, policy groups, roles, demo users,
 * and profile-level permission overrides on startup.
 */
@Component
@Order(15)
public class AuthorizationCatalogLoader implements CommandLineRunner {

    private final AppRepository appRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final ApplicationRoleRepository applicationRoleRepository;
    private final PolicyGroupRepository policyGroupRepository;
    private final PolicyGroupPermissionRepository policyGroupPermissionRepository;
    private final RolePolicyGroupRepository rolePolicyGroupRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-authorization-catalog:${platform.db.load-policy-data:true}}")
    private boolean loadAuthorizationCatalog;

    public AuthorizationCatalogLoader(AppRepository appRepository,
                                        RoleRepository roleRepository,
                                        PermissionRepository permissionRepository,
                                        ApplicationPermissionRepository applicationPermissionRepository,
                                        ApplicationRoleRepository applicationRoleRepository,
                                        PolicyGroupRepository policyGroupRepository,
                                        PolicyGroupPermissionRepository policyGroupPermissionRepository,
                                        RolePolicyGroupRepository rolePolicyGroupRepository,
                                        UserRepository userRepository,
                                        UserProfileRepository profileRepository,
                                        ProfileRoleRepository profileRoleRepository,
                                        ProfilePermissionRepository profilePermissionRepository,
                                        MenuRepository menuRepository,
                                        RoleMenuRepository roleMenuRepository,
                                        PasswordEncoder passwordEncoder) {
        this.appRepository = appRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.applicationPermissionRepository = applicationPermissionRepository;
        this.applicationRoleRepository = applicationRoleRepository;
        this.policyGroupRepository = policyGroupRepository;
        this.policyGroupPermissionRepository = policyGroupPermissionRepository;
        this.rolePolicyGroupRepository = rolePolicyGroupRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.profileRoleRepository = profileRoleRepository;
        this.profilePermissionRepository = profilePermissionRepository;
        this.menuRepository = menuRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadAuthorizationCatalog) {
            System.out.println("[AuthorizationCatalogLoader] Disabled by platform.db.load-authorization-catalog=false");
            return;
        }

        int permissionsCreated = loadPermissionCatalog();
        CatalogSummary summary = loadAuthorizationCatalog();
        int[] menuStats = loadMenuCatalog();

        System.out.println("[AuthorizationCatalogLoader] Summary:");
        System.out.println("  Explicit catalog permissions created: " + permissionsCreated);
        System.out.println("  Matrix permissions ensured: " + summary.matrixPermissions);
        System.out.println("  Roles / app-roles created: " + summary.rolesCreated);
        System.out.println("  Policy groups created: " + summary.policyGroupsCreated);
        System.out.println("  Role-policy links created: " + summary.rolePolicyLinksCreated);
        System.out.println("  Users created: " + summary.usersCreated);
        System.out.println("  Profile roles created: " + summary.profileRolesCreated);
        System.out.println("  Profile permission extras created: " + summary.profilePermissionsCreated);
        System.out.println("  Menus created: " + menuStats[0]);
        System.out.println("  Role-menu links created: " + menuStats[1]);
    }

    private int loadPermissionCatalog() throws Exception {
        ClassPathResource res = new ClassPathResource("db/permission-catalog.json");
        if (!res.exists()) {
            return 0;
        }

        int created = 0;
        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode permissions = root.get("permissions");
            if (permissions == null || !permissions.isArray()) {
                return 0;
            }

            for (JsonNode node : permissions) {
                created += ensureExplicitPermission(node);
            }
        }
        return created;
    }

    private int ensureExplicitPermission(JsonNode node) {
        String code = node.get("code").asText();
        Optional<Permission> existing = permissionRepository.findFirstByCode(code);
        Permission permission;
        int created = 0;
        if (existing.isPresent()) {
            permission = existing.get();
        } else {
            Permission toSave = Permission.builder()
                .code(code)
                .name(node.get("name").asText())
                .description(textOrNull(node, "description"))
                .appCode(textOrNull(node, "appCode"))
                .module(textOrNull(node, "module"))
                .resource(textOrNull(node, "resource"))
                .action(textOrNull(node, "action"))
                .build();
            IdGenerator.assignIfAbsent(toSave);
            permission = permissionRepository.save(toSave);
            created = 1;
        }

        linkPermissionToApp(permission, textOrNull(node, "appCode"));
        return created;
    }

    private CatalogSummary loadAuthorizationCatalog() throws Exception {
        ClassPathResource res = new ClassPathResource("db/authorization-catalog.json");
        if (!res.exists()) {
            System.out.println("[AuthorizationCatalogLoader] authorization-catalog.json not found");
            return CatalogSummary.empty();
        }

        CatalogSummary summary = new CatalogSummary();
        Map<String, Map<String, Permission>> matrixPermissionsByApp = new LinkedHashMap<>();

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);

            JsonNode matrices = root.get("permissionMatrices");
            if (matrices != null && matrices.isArray()) {
                for (JsonNode matrixNode : matrices) {
                    String appCode = matrixNode.get("appCode").asText();
                    List<String> modules = readStringList(matrixNode.get("modules"));
                    List<String> actions = readStringList(matrixNode.get("actions"));
                    appRepository.findByCode(appCode).ifPresent(app -> {
                        Map<String, Permission> byCode = ensurePermissionMatrix(app, appCode, modules, actions);
                        matrixPermissionsByApp.put(appCode, byCode);
                        summary.matrixPermissions += byCode.size();
                    });
                }
            }

            Map<String, Role> roleByCode = loadRoles(root, summary);
            loadApplicationRoles(root, summary);
            summary.policyGroupsCreated += loadStaticPolicyGroups(root);
            summary.rolePolicyLinksCreated += loadRolePolicyGroups(root);
            summary.policyGroupsCreated += loadAppRoleAssignments(root, roleByCode, matrixPermissionsByApp);
            loadUsers(root, roleByCode, summary);
            loadProfilePermissionExtras(root, matrixPermissionsByApp, summary);
        }

        return summary;
    }

    private Map<String, Role> loadRoles(JsonNode root, CatalogSummary summary) {
        Map<String, Role> roleByCode = new LinkedHashMap<>();
        JsonNode roles = root.get("roles");
        if (roles == null || !roles.isArray()) {
            return roleByCode;
        }

        for (JsonNode roleNode : roles) {
            String code = roleNode.get("code").asText();
            Role role = roleRepository.findAll().stream()
                .filter(r -> code.equals(r.getCode()))
                .findFirst()
                .orElseGet(() -> {
                    Role created = Role.builder()
                        .code(code)
                        .name(roleNode.get("name").asText())
                        .description(textOrNull(roleNode, "description"))
                        .build();
                    IdGenerator.assignIfAbsent(created);
                    summary.rolesCreated++;
                    return roleRepository.save(created);
                });
            roleByCode.put(code, role);
        }
        return roleByCode;
    }

    private void loadApplicationRoles(JsonNode root, CatalogSummary summary) {
        JsonNode applicationRoles = root.get("applicationRoles");
        if (applicationRoles == null || !applicationRoles.isArray()) {
            return;
        }

        for (JsonNode node : applicationRoles) {
            String appCode = node.get("appCode").asText();
            String roleCode = node.get("roleCode").asText();
            Optional<App> appOpt = appRepository.findByCode(appCode);
            Role role = findRoleByCode(roleCode);
            if (appOpt.isEmpty() || role == null) {
                continue;
            }

            App app = appOpt.get();
            boolean exists = applicationRoleRepository.findAll().stream()
                .anyMatch(ar -> ar.getAppId().equals(app.getId()) && ar.getRoleId().equals(role.getId()));
            if (!exists) {
                ApplicationRole ar = ApplicationRole.builder()
                    .appId(app.getId())
                    .roleId(role.getId())
                    .build();
                IdGenerator.assignIfAbsent(ar);
                applicationRoleRepository.save(ar);
                summary.rolesCreated++;
            }
        }
    }

    private int loadStaticPolicyGroups(JsonNode root) {
        int created = 0;
        JsonNode groups = root.get("policyGroups");
        if (groups == null || !groups.isArray()) {
            return created;
        }

        for (JsonNode groupNode : groups) {
            String appCode = groupNode.get("appCode").asText();
            String code = groupNode.get("code").asText();
            Optional<App> appOpt = appRepository.findByCode(appCode);
            if (appOpt.isEmpty()) {
                continue;
            }
            App app = appOpt.get();
            boolean wasNew = policyGroupRepository.findByAppIdAndCode(app.getId(), code).isEmpty();
            PolicyGroup group = policyGroupRepository.findByAppIdAndCode(app.getId(), code).orElseGet(() -> {
                PolicyGroup pg = PolicyGroup.builder()
                    .appId(app.getId())
                    .code(code)
                    .name(groupNode.get("name").asText())
                    .description(textOrNull(groupNode, "description"))
                    .build();
                IdGenerator.assignIfAbsent(pg);
                return policyGroupRepository.save(pg);
            });
            if (wasNew) {
                created++;
            }

            JsonNode permissionCodes = groupNode.get("permissions");
            if (permissionCodes != null && permissionCodes.isArray()) {
                for (JsonNode permCodeNode : permissionCodes) {
                    linkPolicyGroupToPermissionCode(group, permCodeNode.asText());
                }
            }
        }
        return created;
    }

    private int loadRolePolicyGroups(JsonNode root) {
        int created = 0;
        JsonNode rolePolicyGroups = root.get("rolePolicyGroups");
        if (rolePolicyGroups == null || !rolePolicyGroups.isArray()) {
            return created;
        }

        for (JsonNode mappingNode : rolePolicyGroups) {
            String appCode = mappingNode.get("appCode").asText();
            String roleCode = mappingNode.get("roleCode").asText();
            String policyGroupCode = mappingNode.get("policyGroupCode").asText();

            Optional<App> appOpt = appRepository.findByCode(appCode);
            Role role = findRoleByCode(roleCode);
            if (appOpt.isEmpty() || role == null) {
                continue;
            }

            Optional<PolicyGroup> groupOpt = policyGroupRepository.findByAppIdAndCode(appOpt.get().getId(), policyGroupCode);
            if (groupOpt.isEmpty()) {
                continue;
            }

            if (!rolePolicyGroupRepository.existsByRoleIdAndPolicyGroupId(role.getId(), groupOpt.get().getId())) {
                RolePolicyGroup mapping = RolePolicyGroup.builder()
                    .roleId(role.getId())
                    .policyGroupId(groupOpt.get().getId())
                    .build();
                IdGenerator.assignIfAbsent(mapping);
                rolePolicyGroupRepository.save(mapping);
                created++;
            }
        }
        return created;
    }

    private int loadAppRoleAssignments(JsonNode root,
                                       Map<String, Role> roleByCode,
                                       Map<String, Map<String, Permission>> matrixPermissionsByApp) {
        int policyGroupsCreated = 0;
        JsonNode appRoleAssignments = root.get("appRoleAssignments");
        if (appRoleAssignments == null || !appRoleAssignments.isObject()) {
            return policyGroupsCreated;
        }

        Iterator<Map.Entry<String, JsonNode>> appFields = appRoleAssignments.fields();
        while (appFields.hasNext()) {
            Map.Entry<String, JsonNode> appEntry = appFields.next();
            String appCode = appEntry.getKey();
            Optional<App> appOpt = appRepository.findByCode(appCode);
            if (appOpt.isEmpty()) {
                continue;
            }
            App app = appOpt.get();

            MatrixContext matrixContext = resolveMatrixContext(appEntry.getValue(), matrixPermissionsByApp.get(appCode), appCode);
            if (matrixContext == null) {
                continue;
            }

            JsonNode roleAssignments = appEntry.getValue();
            Iterator<Map.Entry<String, JsonNode>> roleFields = roleAssignments.fields();
            while (roleFields.hasNext()) {
                Map.Entry<String, JsonNode> roleEntry = roleFields.next();
                String roleCode = roleEntry.getKey();
                Role role = roleByCode.get(roleCode);
                if (role == null) {
                    continue;
                }

                Set<String> codes = resolveRolePermissionCodes(roleEntry.getValue(), matrixContext);
                String policyGroupCode = appCode.toUpperCase() + "_ROLE_" + roleCode;
                boolean[] groupWasNew = {false};
                PolicyGroup group = policyGroupRepository.findByAppIdAndCode(app.getId(), policyGroupCode)
                    .orElseGet(() -> {
                        PolicyGroup pg = PolicyGroup.builder()
                            .appId(app.getId())
                            .code(policyGroupCode)
                            .name(role.getName() + " Permissions")
                            .description(appCode.toUpperCase() + " permission matrix for role " + roleCode)
                            .build();
                        IdGenerator.assignIfAbsent(pg);
                        groupWasNew[0] = true;
                        return policyGroupRepository.save(pg);
                    });
                if (groupWasNew[0]) {
                    policyGroupsCreated++;
                }

                linkPolicyGroupPermissions(group, codes, matrixContext.permissionByCode());

                if (!rolePolicyGroupRepository.existsByRoleIdAndPolicyGroupId(role.getId(), group.getId())) {
                    RolePolicyGroup rpg = RolePolicyGroup.builder()
                        .roleId(role.getId())
                        .policyGroupId(group.getId())
                        .build();
                    IdGenerator.assignIfAbsent(rpg);
                    rolePolicyGroupRepository.save(rpg);
                }
            }
        }
        return policyGroupsCreated;
    }

    private void loadUsers(JsonNode root, Map<String, Role> roleByCode, CatalogSummary summary) {
        JsonNode users = root.get("users");
        if (users == null || !users.isArray()) {
            return;
        }

        for (JsonNode userNode : users) {
            String appCode = userNode.get("appCode").asText();
            String email = userNode.get("email").asText();
            Optional<App> appOpt = appRepository.findByCode(appCode);
            if (appOpt.isEmpty()) {
                continue;
            }
            App app = appOpt.get();

            if (userRepository.findByEmailAndAppId(email, app.getId()).isPresent()) {
                continue;
            }

            User user = User.builder()
                .appId(app.getId())
                .email(email)
                .password(passwordEncoder.encode(userNode.get("password").asText()))
                .firstName(userNode.get("firstName").asText())
                .lastName(userNode.get("lastName").asText())
                .mobile(textOrNull(userNode, "mobile"))
                .birthDate("1985-01-01")
                .build();
            IdGenerator.assignIfAbsent(user);
            user = userRepository.save(user);
            summary.usersCreated++;

            UserProfile profile = UserProfile.builder()
                .userId(user.getId())
                .appId(app.getId())
                .registrationStatus(RegistrationStatusEnum.Created)
                .registeredAt(Instant.now())
                .build();
            IdGenerator.assignIfAbsent(profile);
            profile = profileRepository.save(profile);

            String assignRole = textOrNull(userNode, "assignRole");
            if (assignRole != null) {
                Role role = roleByCode.get(assignRole);
                if (role != null && profileRoleRepository.findByProfileIdAndIsActiveTrue(profile.getId()).stream()
                    .noneMatch(pr -> pr.getRoleId().equals(role.getId()))) {
                    ProfileRole pr = ProfileRole.builder()
                        .profileId(profile.getId())
                        .roleId(role.getId())
                        .grantedAt(Instant.now())
                        .build();
                    IdGenerator.assignIfAbsent(pr);
                    profileRoleRepository.save(pr);
                    summary.profileRolesCreated++;
                }
            }
        }
    }

    private void loadProfilePermissionExtras(JsonNode root,
                                             Map<String, Map<String, Permission>> matrixPermissionsByApp,
                                             CatalogSummary summary) {
        JsonNode extras = root.get("profilePermissionExtras");
        if (extras == null || !extras.isArray()) {
            return;
        }

        for (JsonNode extra : extras) {
            String appCode = extra.get("appCode").asText();
            String email = extra.get("email").asText();
            String permissionCode = extra.get("permissionCode").asText();
            PermissionEffectEnum effect = PermissionEffectEnum.valueOf(extra.get("effect").asText());

            Optional<App> appOpt = appRepository.findByCode(appCode);
            if (appOpt.isEmpty()) {
                continue;
            }
            App app = appOpt.get();

            Optional<User> userOpt = userRepository.findByEmailAndAppId(email, app.getId());
            if (userOpt.isEmpty()) {
                continue;
            }

            Permission permission = resolvePermission(permissionCode, matrixPermissionsByApp.get(appCode));
            if (permission == null) {
                permission = permissionRepository.findFirstByCode(permissionCode).orElse(null);
            }
            if (permission == null) {
                continue;
            }

            Optional<UserProfile> profileOpt = profileRepository
                .findByUserIdAndAppId(userOpt.get().getId(), app.getId());
            if (profileOpt.isEmpty()) {
                continue;
            }

            UUID profileId = profileOpt.get().getId();
            if (profilePermissionRepository.existsByProfileIdAndPermissionId(profileId, permission.getId())) {
                continue;
            }

            ProfilePermission pp = ProfilePermission.builder()
                .profileId(profileId)
                .permissionId(permission.getId())
                .effect(effect)
                .build();
            IdGenerator.assignIfAbsent(pp);
            profilePermissionRepository.save(pp);
            summary.profilePermissionsCreated++;
        }
    }

    private Map<String, Permission> ensurePermissionMatrix(App app,
                                                           String appCode,
                                                           List<String> modules,
                                                           List<String> actions) {
        Map<String, Permission> byCode = new LinkedHashMap<>();
        for (String module : modules) {
            for (String action : actions) {
                String code = PermissionCodes.of(appCode, module, module, action);
                Permission permission = permissionRepository.findFirstByCode(code).orElseGet(() -> {
                    Permission p = Permission.builder()
                        .code(code)
                        .name(formatPermissionName(action, module))
                        .description(appCode.toUpperCase() + " " + module + " module — " + action)
                        .appCode(appCode)
                        .module(module)
                        .resource(module)
                        .action(action)
                        .build();
                    IdGenerator.assignIfAbsent(p);
                    return permissionRepository.save(p);
                });
                byCode.put(code, permission);
                linkPermissionToApp(permission, appCode);
            }
        }
        return byCode;
    }

    private void linkPermissionToApp(Permission permission, String appCode) {
        if (appCode == null) {
            return;
        }
        appRepository.findByCode(appCode).ifPresent(app -> {
            boolean linked = applicationPermissionRepository.findAll().stream()
                .anyMatch(ap -> ap.getAppId().equals(app.getId())
                    && ap.getPermissionId().equals(permission.getId()));
            if (!linked) {
                ApplicationPermission ap = ApplicationPermission.builder()
                    .appId(app.getId())
                    .permissionId(permission.getId())
                    .build();
                IdGenerator.assignIfAbsent(ap);
                applicationPermissionRepository.save(ap);
            }
        });
    }

    private void linkPolicyGroupToPermissionCode(PolicyGroup group, String permCode) {
        permissionRepository.findFirstByCode(permCode).ifPresent(permission -> {
            if (!policyGroupPermissionRepository.existsByPolicyGroupIdAndPermissionId(group.getId(), permission.getId())) {
                PolicyGroupPermission mapping = PolicyGroupPermission.builder()
                    .policyGroupId(group.getId())
                    .permissionId(permission.getId())
                    .build();
                IdGenerator.assignIfAbsent(mapping);
                policyGroupPermissionRepository.save(mapping);
            }
        });
    }

    private void linkPolicyGroupPermissions(PolicyGroup group,
                                            Set<String> permissionCodes,
                                            Map<String, Permission> permissionByCode) {
        for (String code : permissionCodes) {
            Permission permission = permissionByCode != null ? permissionByCode.get(code) : null;
            if (permission == null) {
                permission = permissionRepository.findFirstByCode(code).orElse(null);
            }
            if (permission == null) {
                continue;
            }
            if (!policyGroupPermissionRepository.existsByPolicyGroupIdAndPermissionId(group.getId(), permission.getId())) {
                PolicyGroupPermission pgp = PolicyGroupPermission.builder()
                    .policyGroupId(group.getId())
                    .permissionId(permission.getId())
                    .build();
                IdGenerator.assignIfAbsent(pgp);
                policyGroupPermissionRepository.save(pgp);
            }
        }
    }

    private MatrixContext resolveMatrixContext(JsonNode roleAssignmentsNode,
                                               Map<String, Permission> matrixPermissions,
                                               String appCode) {
        if (matrixPermissions == null || matrixPermissions.isEmpty()) {
            return null;
        }

        List<String> modules = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        for (String code : matrixPermissions.keySet()) {
            String[] parts = code.split(":");
            if (parts.length >= 4 && appCode.equals(parts[0])) {
                modules.add(parts[1]);
                actions.add(parts[3]);
            }
        }
        return new MatrixContext(
            modules.stream().distinct().toList(),
            actions.stream().distinct().toList(),
            matrixPermissions
        );
    }

    private Set<String> resolveRolePermissionCodes(JsonNode assignment, MatrixContext matrixContext) {
        Set<String> allCodes = allPermissionCodes(matrixContext);
        if (assignment.has("grantAll") && assignment.get("grantAll").asBoolean()) {
            Set<String> granted = new LinkedHashSet<>(allCodes);
            if (assignment.has("denyPatterns")) {
                for (JsonNode deny : assignment.get("denyPatterns")) {
                    granted.removeAll(expandPattern(deny.asText(), matrixContext));
                }
            }
            return granted;
        }

        Set<String> granted = new LinkedHashSet<>();
        if (assignment.has("grants")) {
            for (JsonNode grant : assignment.get("grants")) {
                granted.addAll(expandPattern(grant.asText(), matrixContext));
            }
        }
        return granted;
    }

    private Set<String> expandPattern(String pattern, MatrixContext matrixContext) {
        Set<String> result = new LinkedHashSet<>();
        if (pattern == null || pattern.isBlank()) {
            return result;
        }
        String[] parts = pattern.split(":", 2);
        if (parts.length != 2) {
            return result;
        }
        String modulePart = parts[0];
        String actionPart = parts[1];

        List<String> modules = "*".equals(modulePart) ? matrixContext.modules() : List.of(modulePart);
        List<String> actions = "*".equals(actionPart) ? matrixContext.actions() : List.of(actionPart);

        for (String module : modules) {
            for (String action : actions) {
                matrixContext.permissionByCode().keySet().stream()
                    .filter(code -> code.endsWith(":" + module + ":" + action))
                    .forEach(result::add);
            }
        }
        return result;
    }

    private Set<String> allPermissionCodes(MatrixContext matrixContext) {
        return new LinkedHashSet<>(matrixContext.permissionByCode().keySet());
    }

    private Permission resolvePermission(String permissionCode, Map<String, Permission> matrixPermissions) {
        if (matrixPermissions != null && matrixPermissions.containsKey(permissionCode)) {
            return matrixPermissions.get(permissionCode);
        }
        return null;
    }

    private Role findRoleByCode(String roleCode) {
        return roleRepository.findAll().stream()
            .filter(role -> roleCode.equals(role.getCode()))
            .findFirst()
            .orElse(null);
    }

    private static List<String> readStringList(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            values.add(item.asText());
        }
        return values;
    }

    private static String formatPermissionName(String action, String module) {
        String actionLabel = switch (action) {
            case "read" -> "View";
            case "create" -> "Create";
            case "update" -> "Update";
            case "delete" -> "Delete";
            case "manage" -> "Manage";
            case "approve" -> "Approve";
            case "export" -> "Export";
            case "import" -> "Import";
            default -> action;
        };
        String moduleLabel = Arrays.stream(module.split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
        return actionLabel + " " + moduleLabel;
    }

    private static String textOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    /** @return [menusCreated, roleMenuLinksCreated] */
    private int[] loadMenuCatalog() throws Exception {
        ClassPathResource res = new ClassPathResource("db/menu-catalog.json");
        if (!res.exists()) {
            return new int[]{0, 0};
        }
        int menusCreated = 0;
        int linksCreated = 0;
        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            Map<String, Map<String, Menu>> menusByApp = new LinkedHashMap<>();

            JsonNode menus = root.get("menus");
            if (menus != null && menus.isArray()) {
                for (JsonNode node : menus) {
                    String appCode = node.get("appCode").asText();
                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    if (appOpt.isEmpty()) {
                        continue;
                    }
                    App app = appOpt.get();
                    String code = node.get("code").asText();
                    boolean[] wasNew = {false};
                    Menu menu = menuRepository.findByAppIdAndCode(app.getId(), code).orElseGet(() -> {
                        Menu created = Menu.builder()
                            .appId(app.getId())
                            .code(code)
                            .name(node.get("name").asText())
                            .path(textOrNull(node, "path"))
                            .moduleCode(textOrNull(node, "moduleCode"))
                            .parentCode(textOrNull(node, "parentCode"))
                            .sortOrder(node.has("sortOrder") ? node.get("sortOrder").asInt() : 0)
                            .build();
                        created.setIsActive(true);
                        IdGenerator.assignIfAbsent(created);
                        wasNew[0] = true;
                        return menuRepository.save(created);
                    });
                    if (wasNew[0]) {
                        menusCreated++;
                    }
                    menusByApp.computeIfAbsent(appCode, k -> new LinkedHashMap<>()).put(code, menu);
                }
            }

            JsonNode roleMenus = root.get("roleMenus");
            if (roleMenus != null && roleMenus.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> appFields = roleMenus.fields();
                while (appFields.hasNext()) {
                    Map.Entry<String, JsonNode> appEntry = appFields.next();
                    String appCode = appEntry.getKey();
                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    Map<String, Menu> appMenus = menusByApp.getOrDefault(appCode, Map.of());
                    if (appOpt.isEmpty() || appMenus.isEmpty()) {
                        continue;
                    }
                    Iterator<Map.Entry<String, JsonNode>> roleFields = appEntry.getValue().fields();
                    while (roleFields.hasNext()) {
                        Map.Entry<String, JsonNode> roleEntry = roleFields.next();
                        Role role = findRoleByCode(roleEntry.getKey());
                        if (role == null) {
                            continue;
                        }
                        List<String> menuCodes = readStringList(roleEntry.getValue());
                        Collection<Menu> targets;
                        if (menuCodes.size() == 1 && "*".equals(menuCodes.get(0))) {
                            targets = appMenus.values();
                        } else {
                            targets = menuCodes.stream()
                                .map(appMenus::get)
                                .filter(Objects::nonNull)
                                .toList();
                        }
                        for (Menu menu : targets) {
                            if (!roleMenuRepository.existsByRoleIdAndMenuId(role.getId(), menu.getId())) {
                                RoleMenu link = RoleMenu.builder()
                                    .roleId(role.getId())
                                    .menuId(menu.getId())
                                    .build();
                                link.setIsActive(true);
                                IdGenerator.assignIfAbsent(link);
                                roleMenuRepository.save(link);
                                linksCreated++;
                            }
                        }
                    }
                }
            }
        }
        return new int[]{menusCreated, linksCreated};
    }

    private record MatrixContext(List<String> modules, List<String> actions, Map<String, Permission> permissionByCode) {}

    private static final class CatalogSummary {
        int matrixPermissions;
        int rolesCreated;
        int policyGroupsCreated;
        int rolePolicyLinksCreated;
        int usersCreated;
        int profileRolesCreated;
        int profilePermissionsCreated;

        static CatalogSummary empty() {
            return new CatalogSummary();
        }
    }
}
