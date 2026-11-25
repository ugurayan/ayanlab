package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.MeterTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "MeterReading", schema = "ipms", indexes = @Index(name = "idx_ipms_meter_reading_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "unitLabel")
    private String unitLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "meterType")
    private MeterTypeEnum meterType;

    @Column(name = "previousValue")
    @Builder.Default
    private BigDecimal previousValue = BigDecimal.ZERO;

    @Column(name = "currentValue")
    @Builder.Default
    private BigDecimal currentValue = BigDecimal.ZERO;

    @Column(name = "readAt")
    private Instant readAt;

    @JsonProperty("previous")
    public BigDecimal getPrevious() {
        return previousValue;
    }

    @JsonProperty("current")
    public BigDecimal getCurrent() {
        return currentValue;
    }
}