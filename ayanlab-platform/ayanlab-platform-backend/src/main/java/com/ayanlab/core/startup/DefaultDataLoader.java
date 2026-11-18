package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.data.enums.AppSubscriptionModeEnum;
import com.ayanlab.core.data.enums.ProfileTenancyEnum;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.util.ProfileSlugHelper;
import com.ayanlab.core.data.enums.RegistrationStatusEnum;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.util.IdGenerator;
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

@AuthSeedSurface
@Component
@Order(10)
public class DefaultDataLoader implements CommandLineRunner {

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-default-data:true}")
    private boolean loadDefault;

    public DefaultDataLoader(AppRepository appRepository,
                             UserRepository userRepository,
                             UserProfileRepository profileRepository,
                             RoleRepository roleRepository,
                             PermissionRepository permissionRepository,
                             ApplicationPermissionRepository applicationPermissionRepository,
                             ProfileRoleRepository profileRoleRepository,
                             PasswordEncoder passwordEncoder) {
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.applicationPermissionRepository = applicationPermissionRepository;
        this.profileRoleRepository = profileRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadDefault) {
            System.out.println("[DefaultDataLoader] Disabled by platform.db.load-default-data=false");
            return;
        }

        ClassPathResource res = new ClassPathResource("db/default-data.json");
        if (!res.exists()) {
            System.out.println("[DefaultDataLoader] default-data.json not found in classpath");
            return;
        }

        int appsCreated = 0, usersCreated = 0, profilesCreated = 0, rolesCreated = 0, permissionsCreated = 0,
            appPermsCreated = 0, profileRolesCreated = 0;

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);

            // roles are templates; a Role row is created per app from "applicationRoles"
            Map<String, JsonNode> roleTemplates = new LinkedHashMap<>();
            JsonNode roles = root.get("roles");
            if (roles != null && roles.isArray()) {
                for (JsonNode r : roles) {
                    roleTemplates.put(r.get("code").asText(), r);
                }
            }

            // permissions
            JsonNode perms = root.get("permissions");
            if (perms != null && perms.isArray()) {
                for (JsonNode p : perms) {
                    String code = p.get("code").asText();
                    Optional<Permission> ex = permissionRepository.findAll().stream().filter(pp -> code.equals(pp.getCode())).findFirst();
                    if (ex.isEmpty()) {
                        Permission perm = Permission.builder().code(code).name(p.get("name").asText()).description(p.has("description") ? p.get("description").asText() : null).build();
                        if (p.has("id")) {
                            try { perm.setId(UUID.fromString(p.get("id").asText())); } catch (Exception ignore) {}
                        }
                        IdGenerator.assignIfAbsent(perm);
                        permissionRepository.save(perm);
                        permissionsCreated++;
                    }
                }
            }

            // apps
            JsonNode apps = root.get("applications");
            if (apps != null && apps.isArray()) {
                for (JsonNode a : apps) {
                    String code = a.get("code").asText();
                    String name = a.get("name").asText();
                    Optional<App> existing = appRepository.findByCode(code);
                    if (existing.isEmpty()) {
                        App.AppBuilder appBuilder = App.builder()
                            .code(code)
                            .name(name)
                            .description(a.has("description") ? a.get("description").asText() : null);
                        if (a.has("apiBasePath")) {
                            appBuilder.apiBasePath(a.get("apiBasePath").asText());
                        }
                        if (a.has("subscriptionMode")) {
                            try {
                                appBuilder.subscriptionMode(AppSubscriptionModeEnum.valueOf(a.get("subscriptionMode").asText()));
                            } catch (Exception ignore) {}
                        }
                        if (a.has("multiTenantEnabled")) {
                            appBuilder.multiTenantEnabled(a.get("multiTenantEnabled").asBoolean());
                        }
                        App app = appBuilder.build();
                        if (a.has("id")) {
                            try { app.setId(UUID.fromString(a.get("id").asText())); } catch (Exception ignore) { }
                        }
                        IdGenerator.assignIfAbsent(app);
                        appRepository.save(app);
                        appsCreated++;
                    }
                }
            }

            // applicationRoles
            JsonNode appRoles = root.get("applicationRoles");
            if (appRoles != null && appRoles.isArray()) {
                for (JsonNode ar : appRoles) {
                    String appCode = ar.get("appCode").asText();
                    String roleCode = ar.get("roleCode").asText();
                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    JsonNode template = roleTemplates.get(roleCode);
                    if (appOpt.isPresent() && template != null) {
                        App app = appOpt.get();
                        Optional<Role> existingRole = roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(app.getId(), roleCode);
                        Role role = existingRole.orElseGet(() -> {
                            Role created = Role.builder()
                                .appId(app.getId())
                                .code(roleCode)
                                .name(template.get("name").asText())
                                .description(template.has("description") ? template.get("description").asText() : null)
                                .build();
                            IdGenerator.assignIfAbsent(created);
                            return roleRepository.save(created);
                        });
                        if (existingRole.isEmpty()) {
                            rolesCreated++;
                        }
                    }
                }
            }

            // applicationPermissions
            JsonNode appPerms = root.get("applicationPermissions");
            if (appPerms != null && appPerms.isArray()) {
                for (JsonNode ap : appPerms) {
                    String appCode = ap.get("appCode").asText();
                    String permCode = ap.get("permissionCode").asText();
                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    Optional<Permission> permOpt = permissionRepository.findAll().stream().filter(p -> permCode.equals(p.getCode())).findFirst();
                    if (appOpt.isPresent() && permOpt.isPresent()) {
                        App app = appOpt.get(); Permission perm = permOpt.get();
                        boolean exists = applicationPermissionRepository.findAll().stream().anyMatch(x -> x.getAppId().equals(app.getId()) && x.getPermissionId().equals(perm.getId()));
                        if (!exists) {
                            ApplicationPermission apEnt = ApplicationPermission.builder().appId(app.getId()).permissionId(perm.getId()).build();
                            if (ap.has("id")) { try { apEnt.setId(UUID.fromString(ap.get("id").asText())); } catch (Exception ignore) {} }
                            IdGenerator.assignIfAbsent(apEnt);
                            applicationPermissionRepository.save(apEnt); appPermsCreated++;
                        }
                    }
                }
            }

            // users + profiles
            JsonNode users = root.get("users");
            if (users != null && users.isArray()) {
                for (JsonNode u : users) {
                    String appCode = u.get("appCode").asText();
                    String email = u.get("email").asText();
                    String rawPassword = u.get("password").asText();
                    String firstName = u.has("firstName") ? u.get("firstName").asText() : null;
                    String lastName = u.has("lastName") ? u.get("lastName").asText() : null;
                    String mobile = u.has("mobile") ? u.get("mobile").asText() : null;

                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    if (appOpt.isEmpty()) {
                        System.out.println("[DefaultDataLoader] Skipping user creation; app not found: " + appCode);
                        continue;
                    }
                    App app = appOpt.get();
                    Optional<User> existingUser = userRepository.findByEmailAndAppId(email, app.getId());
                    if (existingUser.isEmpty()) {
                        User user = User.builder()
                            .appId(app.getId())
                            .email(email)
                            .password(passwordEncoder.encode(rawPassword))
                            .firstName(firstName)
                            .lastName(lastName)
                            .mobile(mobile)
                            .birthDate(u.has("birthDate") ? u.get("birthDate").asText() : "1970-01-01")
                            .build();
                        IdGenerator.assignIfAbsent(user);
                        user = userRepository.save(user); usersCreated++;
                        JsonNode profileNode = u.get("profile");
                        RegistrationStatusEnum status = RegistrationStatusEnum.Created;
                        if (profileNode != null && profileNode.has("registrationStatus")) {
                            try { status = RegistrationStatusEnum.valueOf(profileNode.get("registrationStatus").asText()); } catch (Exception ignore) {}
                        }
                        String profileSlug = ProfileSlugHelper.defaultSlug(null);
                        if (profileNode != null && profileNode.has("profileSlug") && !profileNode.get("profileSlug").asText().isBlank()) {
                            profileSlug = profileNode.get("profileSlug").asText().trim();
                        }
                        UserProfile profile = UserProfile.builder()
                            .userId(user.getId())
                            .appId(app.getId())
                            .profileSlug(profileSlug)
                            .tenancy(ProfileTenancyEnum.PERSONAL)
                            .registrationStatus(status)
                            .registeredAt(Instant.now())
                            .build();
                        IdGenerator.assignIfAbsent(profile);
                        profileRepository.save(profile); profilesCreated++;

                        // optional: assign role to profile if assignRole present
                        if (u.has("assignRole")) {
                            String roleCode = u.get("assignRole").asText();
                            Optional<Role> roleOpt = roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(app.getId(), roleCode);
                            if (roleOpt.isPresent()) {
                                ProfileRole pr = ProfileRole.builder().profileId(profile.getId()).roleId(roleOpt.get().getId()).grantedAt(Instant.now()).build();
                                IdGenerator.assignIfAbsent(pr);
                                profileRoleRepository.save(pr); profileRolesCreated++;
                            }
                        }
                    } else {
                        System.out.println("[DefaultDataLoader] User already exists: " + email + " for app " + appCode);
                    }
                }
            }

            System.out.println("[DefaultDataLoader] Summary:");
            System.out.println("  Apps created: " + appsCreated);
            System.out.println("  Roles created: " + rolesCreated);
            System.out.println("  Permissions created: " + permissionsCreated);
            System.out.println("  ApplicationPermissions created: " + appPermsCreated);
            System.out.println("  Users created: " + usersCreated);
            System.out.println("  Profiles created: " + profilesCreated);
            System.out.println("  ProfileRoles created: " + profileRolesCreated);
        }
    }
}
