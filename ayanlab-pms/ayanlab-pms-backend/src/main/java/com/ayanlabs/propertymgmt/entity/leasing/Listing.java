package com.ayanlabs.propertymgmt.entity.leasing;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.ListingStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "Listing",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_listing_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_listing_property", columnList = "propertyId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "propertyId")
    private UUID propertyId;

    @Column(name = "unitId")
    private UUID unitId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, name = "rentAmountCents")
    private Long rentAmountCents;

    @Column(name = "availableDate")
    private LocalDate availableDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ListingStatusEnum status = ListingStatusEnum.DRAFT;

    @Column(name = "publishedAt")
    private Instant publishedAt;
}
