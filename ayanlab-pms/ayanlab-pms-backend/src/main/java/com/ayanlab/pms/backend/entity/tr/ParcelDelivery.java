package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.ParcelDeliveryStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ParcelDelivery", schema = "ipms", indexes = @Index(name = "idx_ipms_parcel_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelDelivery extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false)
    private String carrier;

    @Column(name = "trackingNumber")
    private String trackingNumber;

    @Column(name = "lockerCode")
    private String lockerCode;

    @NotBlank
    @Column(nullable = false, name = "unitLabel")
    private String unitLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ParcelDeliveryStatusEnum status = ParcelDeliveryStatusEnum.PENDING;
}