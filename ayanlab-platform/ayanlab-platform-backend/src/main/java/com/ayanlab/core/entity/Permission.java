package com.ayanlab.core.entity;

import com.ayanlab.core.authz.AuthzChangeListener;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuthzChangeListener.class)
@Table(name = "Permission", schema = "core")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permission extends BaseEntity {

    @Column(nullable = false)
    private String code;

    private String name;

    private String description;

    /** App segment of {@code app:module:resource:action}. */
    @Column(name = "appCode")
    private String appCode;

    @Column(name = "module")
    private String module;

    @Column(name = "resource")
    private String resource;

    @Column(name = "action")
    private String action;

//    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationPermission> applicationPermissions = new HashSet<>();
//
//    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
//    @Builder.Default
//
//    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ProfilePermission> profilePermissions = new HashSet<>();
}
