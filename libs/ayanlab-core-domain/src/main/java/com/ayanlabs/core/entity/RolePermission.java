package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "RolePermission",
        schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"roleId", "permissionId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RolePermission extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "roleId", nullable = false)
//    private Role role;
    @Column(nullable = false, name = "roleId")
    private UUID roleId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "permissionId", nullable = false)
//    private Permission permission;
    @Column(nullable = false, name = "permissionId")
    private UUID permissionId;
}
