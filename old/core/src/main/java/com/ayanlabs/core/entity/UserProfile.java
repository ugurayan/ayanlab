package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.RegistrationStatusEnum;
import com.ayanlabs.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "UserProfile", schema = "core",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"userId", "appId"})
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile extends BaseEntity {
    @Column(nullable = true, name = "profileName")
    private String profileName;

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
