package com.ayanlabs.propertymgmt.entity.associations;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.AssociationTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "Association",
    schema = "ipms",
    uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "code"}),
    indexes = @Index(name = "idx_ipms_association_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Association extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "associationType")
    private AssociationTypeEnum associationType;

    @Column(name = "addressLine1")
    private String addressLine1;

    @Column(name = "addressLine2")
    private String addressLine2;

    private String city;
    private String state;

    @Column(name = "zipCode")
    private String zipCode;

    @Column(name = "contactEmail")
    private String contactEmail;

    @Column(name = "contactPhone")
    private String contactPhone;
}
