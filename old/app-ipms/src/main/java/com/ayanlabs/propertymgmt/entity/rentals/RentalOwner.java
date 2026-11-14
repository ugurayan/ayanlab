package com.ayanlabs.propertymgmt.entity.rentals;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "RentalOwner",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_rental_owner_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalOwner extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "profileId")
    private UUID profileId;

    @NotBlank
    @Column(nullable = false, name = "firstName")
    private String firstName;

    @NotBlank
    @Column(nullable = false, name = "lastName")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "companyName")
    private String companyName;

    @Column(name = "taxId")
    private String taxId;
}
