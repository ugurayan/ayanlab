package com.ayanlab.core.entity;

import com.ayanlab.core.enums.ProfileTenancyEnum;
import com.ayanlab.core.enums.RegistrationStatusEnum;
import com.ayanlab.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "UserProfile", schema = "core",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"userId", "appId", "profileSlug"})
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile extends BaseEntity {
    @Column(nullable = false, name = "profileSlug")
    private String profileSlug;

    @Column(nullable = true, name = "profileName")
    private String profileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tenancy")
    @Builder.Default
    private ProfileTenancyEnum tenancy = ProfileTenancyEnum.PERSONAL;

    /** Persona in app (tenant, staff, vendor, …) — links to {@link UserType}. */
    @Column(name = "primaryUserTypeId")
    private UUID primaryUserTypeId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userId", nullable = false)
//    private User user;
    @Column(nullable = false, name = "userId")
    private UUID userId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = true, name = "registrationStatus")
    private RegistrationStatusEnum registrationStatus;

    @Column(nullable = true, name = "registeredAt")
    private Instant registeredAt;

    @Column(nullable = true, name = "approvedBy")
    private UUID approvedBy;

    @Column(nullable = true, name = "approvedAt")
    private Instant approvedAt;

    @Column(nullable = true, name = "organizationId")
    private UUID organizationId;

    /** Bumped whenever this profile's roles, policy groups, permission overrides or scopes change. */
    @Column(name = "authzVersion", insertable = false, updatable = false)
    private Long authzVersion;

//    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ProfileRole> profileRoles = new HashSet<>();
//
//    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ProfilePermission> profilePermissions = new HashSet<>();
//
//    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<UserDetail> userDetails = new HashSet<>();
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//        name = "profile_user_types", schema = "core",
//        joinColumns = @JoinColumn(name = "profile_id"),
//        inverseJoinColumns = @JoinColumn(name = "user_type_id")
//    )
//    @Builder.Default
//    private Set<UserType> userTypes = new HashSet<>();
}
