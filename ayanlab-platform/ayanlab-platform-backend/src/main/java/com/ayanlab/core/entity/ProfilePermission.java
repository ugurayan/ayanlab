package com.ayanlab.core.entity;

import com.ayanlab.core.authz.AuthzChangeListener;

import com.ayanlab.core.enums.PermissionEffectEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@EntityListeners(AuthzChangeListener.class)
@Table(name = "ProfilePermission", schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"profileId", "permissionId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfilePermission extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "permissionId", nullable = false)
//    private Permission permission;
    @Column(nullable = false, name = "permissionId")
    private UUID permissionId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "profileId", nullable = false)
//    private UserProfile profile;
    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "effect")
    @Builder.Default
    private PermissionEffectEnum effect = PermissionEffectEnum.GRANT;
}
