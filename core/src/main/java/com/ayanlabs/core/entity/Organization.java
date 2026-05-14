package com.ayanlabs.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Organization", schema = "core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

//    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
//
//    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<OrganizationUser> organizationUsers = new HashSet<>();
}
