package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "ApplicationPermission",
        schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "permissionId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationPermission extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "permissionId", nullable = false)
//    private Permission permission;
    @Column(nullable = false, name = "permissionId")
    private UUID permissionId;
}
