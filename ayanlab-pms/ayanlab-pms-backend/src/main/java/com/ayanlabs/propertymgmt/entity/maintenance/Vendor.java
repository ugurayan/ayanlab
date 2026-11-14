package com.ayanlabs.propertymgmt.entity.maintenance;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "Vendor",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_vendor_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "profileId")
    private UUID profileId;

    @NotBlank
    @Column(nullable = false, name = "companyName")
    private String companyName;

    @Column(name = "contactName")
    private String contactName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "hourlyRateCents")
    private Long hourlyRateCents;
}
