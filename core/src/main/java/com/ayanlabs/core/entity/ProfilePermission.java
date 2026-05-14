package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
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
}
