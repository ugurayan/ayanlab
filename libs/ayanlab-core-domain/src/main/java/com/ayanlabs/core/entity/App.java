package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "App", schema = "core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class App extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    private String code;

    @Column(nullable = false)
    @NotBlank
    private String name;

    private String description;

    @Column(name = "baseUrlPath")
    private String baseUrlPath;

//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationModule> applicationModules = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationRole> applicationRoles = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationPermission> applicationPermissions = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationPermission> applicationUserTypes = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<User> applicationUsers = new HashSet<>();
//
//    @OneToMany(mappedBy = "app", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
}
