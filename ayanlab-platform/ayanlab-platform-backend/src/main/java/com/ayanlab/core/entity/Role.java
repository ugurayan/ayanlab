package com.ayanlab.core.entity;

import com.ayanlab.core.authz.AuthzChangeListener;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@EntityListeners(AuthzChangeListener.class)
@Table(
        name = "Role",
        schema = "core",
        indexes = @Index(name = "idx_role_app_code", columnList = "appId, code"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role extends BaseEntity {

    /** Owning app. Nullable only for legacy rows until {@code AuthorizationModelMigration} backfills them. */
    @Column(name = "appId")
    private UUID appId;

    /** Null for app-wide system roles; set for roles an organization defines for itself. */
    @Column(name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

//    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
//    @Builder.Default
//
//    @OneToMany(mappedBy = "role;", fetch = FetchType.LAZY)
//    @Builder.Default
//
//    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ProfileRole> profileRoles = new HashSet<>();
}
