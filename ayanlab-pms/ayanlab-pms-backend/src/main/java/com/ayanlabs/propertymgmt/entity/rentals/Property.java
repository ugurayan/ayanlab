package com.ayanlabs.propertymgmt.entity.rentals;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.PropertyStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "Property",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_property_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "propertyTypeId")
    private UUID propertyTypeId;

    @NotBlank
    @Column(nullable = false, name = "addressLine1")
    private String addressLine1;

    @Column(name = "addressLine2")
    private String addressLine2;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String state;

    @NotBlank
    @Column(nullable = false, name = "zipCode")
    private String zipCode;

    @Column(name = "country")
    @Builder.Default
    private String country = "US";

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "totalUnits")
    @Builder.Default
    private Integer totalUnits = 1;

    @Column(name = "yearBuilt")
    private Integer yearBuilt;

    @Column(name = "squareFootage")
    private BigDecimal squareFootage;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PropertyStatusEnum status = PropertyStatusEnum.ACTIVE;
}
