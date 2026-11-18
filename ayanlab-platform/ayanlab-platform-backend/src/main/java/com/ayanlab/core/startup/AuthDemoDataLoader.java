package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.data.enums.NotificationTypeEnum;
import com.ayanlab.core.entity.App;
import com.ayanlab.core.entity.ApplicationModule;
import com.ayanlab.core.entity.ApplicationOrganization;
import com.ayanlab.core.entity.ApplicationUser;
import com.ayanlab.core.entity.ApplicationUserType;
import com.ayanlab.core.entity.Module;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.entity.Organization;
import com.ayanlab.core.entity.OrganizationUser;
import com.ayanlab.core.entity.Permission;
import com.ayanlab.core.entity.ProfilePermission;
import com.ayanlab.core.entity.ProfileUserType;
import com.ayanlab.core.entity.Role;
import com.ayanlab.core.entity.TwilioSmsLog;
import com.ayanlab.core.entity.User;
import com.ayanlab.core.entity.UserDetail;
import com.ayanlab.core.entity.UserProfile;
import com.ayanlab.core.entity.UserType;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.util.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Inserts idempotent demo rows for JPA entities not covered by {@link DefaultDataLoader}
 * (modules, organizations, join tables, templates, SMS log, USER role permissions).
 * Runs after {@link DefaultDataLoader} so apps/users from default-data.json exist.
 */
@AuthSeedSurface
@Component
@Order(20)
public class AuthDemoDataLoader implements CommandLineRunner {

    private static final String DEMO_ORG_CODE = "DEMO_ORG";
    private static final String DEMO_MODULE_HOME = "DEMO_HOME";
    private static final String DEMO_MODULE_REPORTS = "DEMO_REPORTS";
    private static final String USER_TYPE_INTERNAL = "INTERNAL";
    private static final String USER_TYPE_CUSTOMER = "CUSTOMER";
    private static final String CORE_APP_CODE = "core";
    private static final String CORE_ADMIN_EMAIL = "admin@core.local";
    private static final String DEMO_SMS_TO = "+15555550100";
    private static final String DEMO_SMS_MESSAGE = "AyanLabs demo SMS seed";
    private static final String NOTIFY_CODE_WELCOME = "WELCOME";
    private static final String DETAIL_KEY_LOCALE = "locale";

    @Value("${platform.db.load-demo-data:true}")
    private boolean loadDemoData;

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserTypeRepository userTypeRepository;
    private final ModuleRepository moduleRepository;
    private final OrganizationRepository organizationRepository;
    private final ApplicationModuleRepository applicationModuleRepository;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final ApplicationUserTypeRepository applicationUserTypeRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final ProfileUserTypeRepository profileUserTypeRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final UserDetailRepository userDetailRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final TwilioSmsLogRepository twilioSmsLogRepository;

    public AuthDemoDataLoader(AppRepository appRepository,
                          UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          RoleRepository roleRepository,
                          PermissionRepository permissionRepository,
                          UserTypeRepository userTypeRepository,
                          ModuleRepository moduleRepository,
                          OrganizationRepository organizationRepository,
                          ApplicationModuleRepository applicationModuleRepository,
                          ApplicationOrganizationRepository applicationOrganizationRepository,
                          ApplicationUserTypeRepository applicationUserTypeRepository,
                          ApplicationUserRepository applicationUserRepository,
                          OrganizationUserRepository organizationUserRepository,
                          ProfileUserTypeRepository profileUserTypeRepository,
                          ProfilePermissionRepository profilePermissionRepository,
                          UserDetailRepository userDetailRepository,
                          NotificationTemplateRepository notificationTemplateRepository,
                          TwilioSmsLogRepository twilioSmsLogRepository) {
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userTypeRepository = userTypeRepository;
        this.moduleRepository = moduleRepository;
        this.organizationRepository = organizationRepository;
        this.applicationModuleRepository = applicationModuleRepository;
        this.applicationOrganizationRepository = applicationOrganizationRepository;
        this.applicationUserTypeRepository = applicationUserTypeRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.organizationUserRepository = organizationUserRepository;
        this.profileUserTypeRepository = profileUserTypeRepository;
        this.profilePermissionRepository = profilePermissionRepository;
        this.userDetailRepository = userDetailRepository;
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.twilioSmsLogRepository = twilioSmsLogRepository;
    }

    @Override
    public void run(String... args) {
        if (!loadDemoData) {
            System.out.println("[AuthDemoDataLoader] Disabled by platform.db.load-demo-data=false");
            return;
        }
        if (appRepository.count() == 0) {
            System.out.println("[AuthDemoDataLoader] Skipped: no applications loaded (enable platform.db.load-default-data)");
            return;
        }

        UserType internal = ensureUserType(USER_TYPE_INTERNAL, "Internal user", "Demo internal classification");
        UserType customer = ensureUserType(USER_TYPE_CUSTOMER, "Customer", "Demo customer classification");

        Module home = ensureModule(DEMO_MODULE_HOME, "Demo home", "Demo module for UI home");
        Module reports = ensureModule(DEMO_MODULE_REPORTS, "Demo reports", "Demo module for reporting");

        Organization org = ensureOrganization(DEMO_ORG_CODE, "Demo Organization", "Seeded org for local development");
        Organization sunrise = ensureOrganization("sunrise", "Sunrise Property Management LLC", "IPMS demo property management company");
        Organization bayarea = ensureOrganization("bayarea", "Bay Area Rentals Inc.", "IPMS demo rental company");
        Organization hank = ensureOrganization("hankhand", "Hank the Handyman Services", "IPMS demo maintenance organization");

        Optional<App> coreAppOpt = appRepository.findByCode(CORE_APP_CODE);
        Optional<App> ipmsAppOpt = appRepository.findByCode("ipms");
        if (coreAppOpt.isEmpty()) {
            System.out.println("[AuthDemoDataLoader] Skipped linking: app 'core' not found");
            return;
        }
        App coreApp = coreAppOpt.get();

        linkApplicationUserType(coreApp.getId(), internal.getId());
        linkApplicationUserType(coreApp.getId(), customer.getId());
        userRepository.findAll().forEach(user -> {
            if (user.getId() != null && user.getAppId() != null) {
                linkApplicationUser(user.getAppId(), user.getId());
            }
        });

        linkApplicationModule(coreApp.getId(), home.getId());
        linkApplicationModule(coreApp.getId(), reports.getId());
        linkApplicationOrganization(coreApp.getId(), org.getId());

        ipmsAppOpt.ifPresent(ipmsApp -> {
            linkApplicationOrganization(ipmsApp.getId(), org.getId());
            linkApplicationOrganization(ipmsApp.getId(), sunrise.getId());
            linkApplicationOrganization(ipmsApp.getId(), bayarea.getId());
            linkApplicationOrganization(ipmsApp.getId(), hank.getId());
            attachIpmsAdminToDemoOrg(ipmsApp.getId(), org.getId());
            System.out.println("[AuthDemoDataLoader] IPMS demo organizations linked: Demo Organization, Sunrise, Bay Area, Hank");
        });

        Optional<User> adminOpt = userRepository.findByEmailAndAppId(CORE_ADMIN_EMAIL, coreApp.getId());
        if (adminOpt.isEmpty()) {
            System.out.println("[AuthDemoDataLoader] Skipped profile demo: user " + CORE_ADMIN_EMAIL + " not found");
            return;
        }
        User admin = adminOpt.get();
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserIdAndAppId(admin.getId(), coreApp.getId());
        if (profileOpt.isEmpty()) {
            System.out.println("[AuthDemoDataLoader] Skipped profile demo: no UserProfile for core admin");
            return;
        }
        UserProfile profile = profileOpt.get();

        if (profile.getOrganizationId() == null) {
            profile.setOrganizationId(org.getId());
            userProfileRepository.save(profile);
        }

        if (!organizationUserRepository.existsByUserIdAndOrgId(admin.getId(), org.getId())) {
            OrganizationUser ou = OrganizationUser.builder().userId(admin.getId()).orgId(org.getId()).build();
            IdGenerator.assignIfAbsent(ou);
            organizationUserRepository.save(ou);
        }
        if (!profileUserTypeRepository.existsByProfileIdAndUserTypeId(profile.getId(), internal.getId())) {
            ProfileUserType put = ProfileUserType.builder().profileId(profile.getId()).userTypeId(internal.getId()).build();
            IdGenerator.assignIfAbsent(put);
            profileUserTypeRepository.save(put);
        }

        permissionRepository.findFirstByCode("VIEW_REPORTS").ifPresent(perm -> {
            if (!profilePermissionRepository.existsByProfileIdAndPermissionId(profile.getId(), perm.getId())) {
                ProfilePermission pp = ProfilePermission.builder().profileId(profile.getId()).permissionId(perm.getId()).build();
                IdGenerator.assignIfAbsent(pp);
                profilePermissionRepository.save(pp);
            }
        });

        if (!userDetailRepository.existsByProfileIdAndDetailKey(profile.getId(), DETAIL_KEY_LOCALE)) {
            UserDetail ud = UserDetail.builder()
                .profileId(profile.getId())
                .detailKey(DETAIL_KEY_LOCALE)
                .detailValue("en-US")
                .build();
            IdGenerator.assignIfAbsent(ud);
            userDetailRepository.save(ud);
        }

        if (!notificationTemplateRepository.existsByAppIdAndCodeAndTypeAndLanguage(
            coreApp.getId(), NOTIFY_CODE_WELCOME, NotificationTypeEnum.EMAIL, "en")) {
            NotificationTemplate nt = NotificationTemplate.builder()
                .appId(coreApp.getId())
                .code(NOTIFY_CODE_WELCOME)
                .type(NotificationTypeEnum.EMAIL)
                .language("en")
                .subject("Welcome to AyanLabs (demo)")
                .body("<p>This is a seeded email template for local development.</p>")
                .description("Demo notification template")
                .build();
            IdGenerator.assignIfAbsent(nt);
            notificationTemplateRepository.save(nt);
        }

        if (!twilioSmsLogRepository.existsByToNumberAndMessage(DEMO_SMS_TO, DEMO_SMS_MESSAGE)) {
            TwilioSmsLog sms = TwilioSmsLog.builder()
                .toNumber(DEMO_SMS_TO)
                .message(DEMO_SMS_MESSAGE)
                .build();
            IdGenerator.assignIfAbsent(sms);
            twilioSmsLogRepository.save(sms);
        }

        System.out.println("[AuthDemoDataLoader] Demo rows ensured for Module, Organization, join tables, templates, SMS log, and USER role permission.");
    }

    private UserType ensureUserType(String code, String name, String description) {
        return userTypeRepository.findByCode(code).orElseGet(() -> {
            UserType ut = UserType.builder().code(code).name(name).description(description).build();
            IdGenerator.assignIfAbsent(ut);
            return userTypeRepository.save(ut);
        });
    }

    private Module ensureModule(String code, String name, String description) {
        return moduleRepository.findByCode(code).orElseGet(() -> {
            Module m = Module.builder().code(code).name(name).description(description).build();
            IdGenerator.assignIfAbsent(m);
            return moduleRepository.save(m);
        });
    }

    private Organization ensureOrganization(String code, String name, String description) {
        return organizationRepository.findFirstByCode(code).map(existing -> {
            boolean dirty = false;
            if (!Boolean.TRUE.equals(existing.getIsActive())) {
                existing.setIsActive(true);
                dirty = true;
            }
            if (name != null && !name.equals(existing.getName())) {
                existing.setName(name);
                dirty = true;
            }
            if (description != null && (existing.getDescription() == null || existing.getDescription().isBlank())) {
                existing.setDescription(description);
                dirty = true;
            }
            return dirty ? organizationRepository.save(existing) : existing;
        }).orElseGet(() -> {
            Organization o = Organization.builder().code(code).name(name).description(description).build();
            o.setIsActive(true);
            IdGenerator.assignIfAbsent(o);
            o.markNew();
            return organizationRepository.save(o);
        });
    }

    private void linkApplicationUser(UUID appId, UUID userId) {
        if (!applicationUserRepository.existsByAppIdAndUserId(appId, userId)) {
            ApplicationUser e = ApplicationUser.builder().appId(appId).userId(userId).build();
            IdGenerator.assignIfAbsent(e);
            e.markNew();
            applicationUserRepository.save(e);
        }
    }

    private void linkApplicationUserType(UUID appId, UUID userTypeId) {
        if (!applicationUserTypeRepository.existsByAppIdAndUserTypeId(appId, userTypeId)) {
            ApplicationUserType e = ApplicationUserType.builder().appId(appId).userTypeId(userTypeId).build();
            IdGenerator.assignIfAbsent(e);
            applicationUserTypeRepository.save(e);
        }
    }

    private void linkApplicationModule(UUID appId, UUID moduleId) {
        if (!applicationModuleRepository.existsByAppIdAndModuleId(appId, moduleId)) {
            ApplicationModule e = ApplicationModule.builder().appId(appId).moduleId(moduleId).build();
            IdGenerator.assignIfAbsent(e);
            e.markNew();
            applicationModuleRepository.save(e);
        }
    }

    private void linkApplicationOrganization(UUID appId, UUID orgId) {
        if (!applicationOrganizationRepository.existsByAppIdAndOrgId(appId, orgId)) {
            ApplicationOrganization e = ApplicationOrganization.builder().appId(appId).orgId(orgId).build();
            IdGenerator.assignIfAbsent(e);
            e.markNew();
            applicationOrganizationRepository.save(e);
        }
    }

    /** Ensure IPMS admin can call org-scoped CRUD (JWT includes oid). */
    private void attachIpmsAdminToDemoOrg(UUID ipmsAppId, UUID orgId) {
        Optional<User> ipmsAdminOpt = userRepository.findByEmailAndAppId("admin@ipms.local", ipmsAppId);
        if (ipmsAdminOpt.isEmpty()) {
            System.out.println("[AuthDemoDataLoader] IPMS admin user not found; skip org attach");
            return;
        }
        User ipmsAdmin = ipmsAdminOpt.get();
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserIdAndAppId(ipmsAdmin.getId(), ipmsAppId);
        if (profileOpt.isEmpty()) {
            System.out.println("[AuthDemoDataLoader] IPMS admin profile not found; skip org attach");
            return;
        }
        UserProfile profile = profileOpt.get();
        if (profile.getOrganizationId() == null || !orgId.equals(profile.getOrganizationId())) {
            profile.setOrganizationId(orgId);
            userProfileRepository.save(profile);
            System.out.println("[AuthDemoDataLoader] Attached admin@ipms.local profile to DEMO_ORG");
        }
        if (!organizationUserRepository.existsByUserIdAndOrgId(ipmsAdmin.getId(), orgId)) {
            OrganizationUser ou = OrganizationUser.builder().userId(ipmsAdmin.getId()).orgId(orgId).build();
            IdGenerator.assignIfAbsent(ou);
            organizationUserRepository.save(ou);
        }
    }

}
