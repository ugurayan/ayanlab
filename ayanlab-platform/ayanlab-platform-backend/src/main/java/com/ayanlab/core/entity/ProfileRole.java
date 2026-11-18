package com.ayanlab.core.entity;

import com.ayanlab.core.authz.AuthzChangeListener;

import com.ayanlab.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuthzChangeListener.class)
@Table(name = "ProfileRole", schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"profileId", "roleId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileRole extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "profileId", nullable = false)
//    private UserProfile profile;
    @Column(nullable = false, name = "profileId")
    private UUID profileId;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "roleId", nullable = false)
//    private Role role;
    @Column(nullable = false, name = "roleId")
    private UUID roleId;

    private Instant grantedAt;
    private UUID grantedBy;
    private Instant expiresAt;
}
