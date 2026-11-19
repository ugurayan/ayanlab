package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "StaffTimesheet", schema = "ipms", indexes = @Index(name = "idx_ipms_timesheet_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffTimesheet extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "staffName")
    private String staffName;

    @Column(nullable = false, name = "workDate")
    private LocalDate workDate;

    @Column(nullable = false, name = "hoursWorked")
    @Builder.Default
    private BigDecimal hoursWorked = BigDecimal.ZERO;
}