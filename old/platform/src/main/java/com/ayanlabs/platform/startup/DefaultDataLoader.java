package com.ayanlabs.platform.startup;

import com.ayanlabs.core.entity.*;
import com.ayanlabs.core.data.enums.RegistrationStatusEnum;
import com.ayanlabs.core.repository.*;
import com.ayanlabs.core.util.IdGenerator;
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

@Component
@Order(10)
public class DefaultDataLoader implements CommandLineRunner {

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationRoleRepository applicationRoleRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
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
                             ApplicationRoleRepository applicationRoleRepository,
                             ApplicationPermissionRepository applicationPermissionRepository,
                             RolePermissionRepository rolePermissionRepository,
                             ProfileRoleRepository profileRoleRepository,
                             PasswordEncoder passwordEncoder) {
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.applicationRoleRepository = applicationRoleRepository;
        this.applicationPermissionRepository = applicationPermissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
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
            appRolesCreated = 0, appPermsCreated = 0, rolePermsCreated = 0, profileRolesCreated = 0;

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);

            // roles
            JsonNode roles = root.get("roles");
            if (roles != null && roles.isArray()) {
                for (JsonNode r : roles) {
                    String code = r.get("code").asText();
                    Optional<Role> ex = roleRepository.findAll().stream().filter(rr -> code.equals(rr.getCode())).findFirst();
                    if (ex.isEmpty()) {
                        Role role = Role.builder().code(code).name(r.get("name").asText()).description(r.has("description") ? r.get("description").asText() : null).build();
                        if (r.has("id")) {
                            try { role.setId(UUID.fromString(r.get("id").asText())); } catch (Exception ignore) {}
                        }
                        IdGenerator.assignIfAbsent(role);
                        roleRepository.save(role);
                        rolesCreated++;
                    }
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
                        App app = App.builder().code(code).name(name).description(a.has("description") ? a.get("description").asText() : null).build();
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
                    Optional<Role> roleOpt = roleRepository.findAll().stream().filter(r -> roleCode.equals(r.getCode())).findFirst();
                    if (appOpt.isPresent() && roleOpt.isPresent()) {
                        App app = appOpt.get(); Role role = roleOpt.get();
                        boolean exists = applicationRoleRepository.findAll().stream().anyMatch(x -> x.getAppId().equals(app.getId()) && x.getRoleId().equals(role.getId()));
                        if (!exists) {
                            ApplicationRole arEnt = ApplicationRole.builder().appId(app.getId()).roleId(role.getId()).build();
                            if (ar.has("id")) { try { arEnt.setId(UUID.fromString(ar.get("id").asText())); } catch (Exception ignore) {} }
                            IdGenerator.assignIfAbsent(arEnt);
                            applicationRoleRepository.save(arEnt); appRolesCreated++;
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
                        UserProfile profile = UserProfile.builder().userId(user.getId()).appId(app.getId()).registrationStatus(status).registeredAt(Instant.now()).build();
                        IdGenerator.assignIfAbsent(profile);
                        profileRepository.save(profile); profilesCreated++;

                        // optional: assign role to profile if assignRole present
                        if (u.has("assignRole")) {
                            String roleCode = u.get("assignRole").asText();
                            Optional<Role> roleOpt = roleRepository.findAll().stream().filter(r -> roleCode.equals(r.getCode())).findFirst();
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

            // role-permissions: create default mappings where applicationPermission exists
            for (Role role : roleRepository.findAll()) {
                for (Permission perm : permissionRepository.findAll()) {
                    if ("ADMIN".equals(role.getCode())) {
                        boolean exists = rolePermissionRepository.findAll().stream().anyMatch(rp -> rp.getRoleId().equals(role.getId()) && rp.getPermissionId().equals(perm.getId()));
                        if (!exists) {
                            RolePermission rp = RolePermission.builder().roleId(role.getId()).permissionId(perm.getId()).build();
                            IdGenerator.assignIfAbsent(rp);
                            rolePermissionRepository.save(rp); rolePermsCreated++;
                        }
                    }
                }
            }

            System.out.println("[DefaultDataLoader] Summary:");
            System.out.println("  Apps created: " + appsCreated);
            System.out.println("  Roles created: " + rolesCreated);
            System.out.println("  Permissions created: " + permissionsCreated);
            System.out.println("  ApplicationRoles created: " + appRolesCreated);
            System.out.println("  ApplicationPermissions created: " + appPermsCreated);
            System.out.println("  Users created: " + usersCreated);
            System.out.println("  Profiles created: " + profilesCreated);
            System.out.println("  ProfileRoles created: " + profileRolesCreated);
            System.out.println("  RolePermissions created: " + rolePermsCreated);
        }
    }
}
