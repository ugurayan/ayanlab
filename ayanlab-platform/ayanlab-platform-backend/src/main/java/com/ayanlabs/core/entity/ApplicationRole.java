package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "ApplicationRole",
        schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "roleId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationRole extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "roleId", nullable = false)
//    private Role role;
    @Column(nullable = false, name = "roleId")
    private UUID roleId;

}
