package com.ayanlab.core.entity;

import com.ayanlab.core.data.enums.AppSubscriptionModeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "App", schema = "core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class App extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    private String code;

    @Column(nullable = false)
    @NotBlank
    private String name;

    private String description;

    @Column(name = "baseUrlPath")
    private String baseUrlPath;

    /** Default API mount for this product, e.g. {@code /pms/api}. */
    @Column(name = "apiBasePath")
    private String apiBasePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscriptionMode")
    private AppSubscriptionModeEnum subscriptionMode;

    /** When true, module-level permissions are enforced via {@code ApplicationModule} + catalog. */
    @Column(name = "moduleAuthEnabled")
    @Builder.Default
    private Boolean moduleAuthEnabled = true;

    /** Supports B2B org tenants + optional personal profiles in the same app. */
    @Column(name = "multiTenantEnabled")
    @Builder.Default
    private Boolean multiTenantEnabled = true;

    /** Bumped whenever roles, policy groups or permissions of this app change. */
    @Column(name = "authzVersion", insertable = false, updatable = false)
    private Long authzVersion;

//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationModule> applicationModules = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationPermission> applicationPermissions = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationPermission> applicationUserTypes = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<User> applicationUsers = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
}
