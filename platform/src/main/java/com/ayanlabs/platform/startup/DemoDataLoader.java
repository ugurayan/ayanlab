package com.ayanlabs.platform.startup;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import com.ayanlabs.core.data.enums.NotificationTypeEnum;
import com.ayanlabs.core.entity.App;
import com.ayanlabs.core.entity.ApplicationModule;
import com.ayanlabs.core.entity.ApplicationOrganization;
import com.ayanlabs.core.entity.ApplicationUserType;
import com.ayanlabs.core.entity.Module;
import com.ayanlabs.core.entity.NotificationTemplate;
import com.ayanlabs.core.entity.Organization;
import com.ayanlabs.core.entity.OrganizationUser;
import com.ayanlabs.core.entity.Permission;
import com.ayanlabs.core.entity.ProfilePermission;
import com.ayanlabs.core.entity.ProfileUserType;
import com.ayanlabs.core.entity.SubscriptionPlan;
import com.ayanlabs.core.entity.Role;
import com.ayanlabs.core.entity.RolePermission;
import com.ayanlabs.core.entity.TwilioSmsLog;
import com.ayanlabs.core.entity.User;
import com.ayanlabs.core.entity.UserDetail;
import com.ayanlabs.core.entity.UserProfile;
import com.ayanlabs.core.entity.UserType;
import com.ayanlabs.core.repository.*;
import com.ayanlabs.core.util.IdGenerator;
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
@Component
@Order(20)
public class DemoDataLoader implements CommandLineRunner {

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
    private final RolePermissionRepository rolePermissionRepository;
    private final UserTypeRepository userTypeRepository;
    private final ModuleRepository moduleRepository;
    private final OrganizationRepository organizationRepository;
    private final ApplicationModuleRepository applicationModuleRepository;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final ApplicationUserTypeRepository applicationUserTypeRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final ProfileUserTypeRepository profileUserTypeRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final UserDetailRepository userDetailRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final TwilioSmsLogRepository twilioSmsLogRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public DemoDataLoader(AppRepository appRepository,
                          UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          RoleRepository roleRepository,
                          PermissionRepository permissionRepository,
                          RolePermissionRepository rolePermissionRepository,
                          UserTypeRepository userTypeRepository,
                          ModuleRepository moduleRepository,
                          OrganizationRepository organizationRepository,
                          ApplicationModuleRepository applicationModuleRepository,
                          ApplicationOrganizationRepository applicationOrganizationRepository,
                          ApplicationUserTypeRepository applicationUserTypeRepository,
                          OrganizationUserRepository organizationUserRepository,
                          ProfileUserTypeRepository profileUserTypeRepository,
                          ProfilePermissionRepository profilePermissionRepository,
                          UserDetailRepository userDetailRepository,
                          NotificationTemplateRepository notificationTemplateRepository,
                          TwilioSmsLogRepository twilioSmsLogRepository,
                          SubscriptionPlanRepository subscriptionPlanRepository) {
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userTypeRepository = userTypeRepository;
        this.moduleRepository = moduleRepository;
        this.organizationRepository = organizationRepository;
        this.applicationModuleRepository = applicationModuleRepository;
        this.applicationOrganizationRepository = applicationOrganizationRepository;
        this.applicationUserTypeRepository = applicationUserTypeRepository;
        this.organizationUserRepository = organizationUserRepository;
        this.profileUserTypeRepository = profileUserTypeRepository;
        this.profilePermissionRepository = profilePermissionRepository;
        this.userDetailRepository = userDetailRepository;
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.twilioSmsLogRepository = twilioSmsLogRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    public void run(String... args) {
        if (!loadDemoData) {
            System.out.println("[DemoDataLoader] Disabled by platform.db.load-demo-data=false");
            return;
        }
        if (appRepository.count() == 0) {
            System.out.println("[DemoDataLoader] Skipped: no applications loaded (enable platform.db.load-default-data)");
            return;
        }

        ensureSubscriptionCatalog();

        UserType internal = ensureUserType(USER_TYPE_INTERNAL, "Internal user", "Demo internal classification");
        UserType customer = ensureUserType(USER_TYPE_CUSTOMER, "Customer", "Demo customer classification");

        Module home = ensureModule(DEMO_MODULE_HOME, "Demo home", "Demo module for UI home");
        Module reports = ensureModule(DEMO_MODULE_REPORTS, "Demo reports", "Demo module for reporting");

        Organization org = ensureOrganization(DEMO_ORG_CODE, "Demo Organization", "Seeded org for local development");

        Optional<App> coreAppOpt = appRepository.findByCode(CORE_APP_CODE);
        if (coreAppOpt.isEmpty()) {
            System.out.println("[DemoDataLoader] Skipped linking: app 'core' not found");
            return;
        }
        App coreApp = coreAppOpt.get();

        linkApplicationUserType(coreApp.getId(), internal.getId());
        linkApplicationUserType(coreApp.getId(), customer.getId());

        linkApplicationModule(coreApp.getId(), home.getId());
        linkApplicationModule(coreApp.getId(), reports.getId());
        linkApplicationOrganization(coreApp.getId(), org.getId());

        seedUserRoleViewReportsPermission();

        Optional<User> adminOpt = userRepository.findByEmailAndAppId(CORE_ADMIN_EMAIL, coreApp.getId());
        if (adminOpt.isEmpty()) {
            System.out.println("[DemoDataLoader] Skipped profile demo: user " + CORE_ADMIN_EMAIL + " not found");
            return;
        }
        User admin = adminOpt.get();
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserIdAndAppId(admin.getId(), coreApp.getId());
        if (profileOpt.isEmpty()) {
            System.out.println("[DemoDataLoader] Skipped profile demo: no UserProfile for core admin");
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

        System.out.println("[DemoDataLoader] Demo rows ensured for Module, Organization, join tables, templates, SMS log, USER role permission, and subscription catalog.");
    }

    private void ensureSubscriptionCatalog() {
        if (subscriptionPlanRepository.findByCode("VAULT_FREE").isEmpty()) {
            SubscriptionPlan p = SubscriptionPlan.builder()
                .code("VAULT_FREE")
                .name("Secure Vault Free")
                .description("Individual free tier for Secure Vault")
                .billingPeriod(BillingPeriodEnum.MONTHLY)
                .amountCents(0L)
                .currency("USD")
                .catalogActive(true)
                .build();
            IdGenerator.assignIfAbsent(p);
            subscriptionPlanRepository.save(p);
        }
        if (subscriptionPlanRepository.findByCode("VAULT_PRO").isEmpty()) {
            SubscriptionPlan p = SubscriptionPlan.builder()
                .code("VAULT_PRO")
                .name("Secure Vault Pro")
                .description("Paid tier with higher limits")
                .billingPeriod(BillingPeriodEnum.YEARLY)
                .amountCents(9999L)
                .currency("USD")
                .catalogActive(true)
                .build();
            IdGenerator.assignIfAbsent(p);
            subscriptionPlanRepository.save(p);
        }
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
        return organizationRepository.findFirstByCode(code).orElseGet(() -> {
            Organization o = Organization.builder().code(code).name(name).description(description).build();
            IdGenerator.assignIfAbsent(o);
            return organizationRepository.save(o);
        });
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
            applicationModuleRepository.save(e);
        }
    }

    private void linkApplicationOrganization(UUID appId, UUID orgId) {
        if (!applicationOrganizationRepository.existsByAppIdAndOrgId(appId, orgId)) {
            ApplicationOrganization e = ApplicationOrganization.builder().appId(appId).orgId(orgId).build();
            IdGenerator.assignIfAbsent(e);
            applicationOrganizationRepository.save(e);
        }
    }

    private void seedUserRoleViewReportsPermission() {
        Optional<Role> userRole = roleRepository.findFirstByCode("USER");
        Optional<Permission> viewReports = permissionRepository.findFirstByCode("VIEW_REPORTS");
        if (userRole.isEmpty() || viewReports.isEmpty()) {
            return;
        }
        UUID rid = userRole.get().getId();
        UUID pid = viewReports.get().getId();
        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(rid, pid)) {
            RolePermission rp = RolePermission.builder().roleId(rid).permissionId(pid).build();
            IdGenerator.assignIfAbsent(rp);
            rolePermissionRepository.save(rp);
        }
    }
}
