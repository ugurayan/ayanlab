package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(
        name = "Role",
        schema = "core")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

//    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<RolePermission> rolePermissions = new HashSet<>();
//
//    @OneToMany(mappedBy = "role;", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationRole> applicationRoles= new HashSet<>();
//
//    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ProfileRole> profileRoles = new HashSet<>();
}
