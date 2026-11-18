package com.ayanlab.pms.backend.entity.maintenance;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.WorkOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "WorkOrder",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_work_order_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_work_order_request", columnList = "maintenanceRequestId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "maintenanceRequestId")
    private UUID maintenanceRequestId;

    @Column(name = "vendorId")
    private UUID vendorId;

    /** Field technician assigned to this work order. */
    @Column(name = "assignedToProfileId")
    private UUID assignedToProfileId;

    @Column(name = "scheduledDate")
    private LocalDate scheduledDate;

    /** Preferred schedule instant (time-of-day aware). */
    @Column(name = "scheduledAt")
    private Instant scheduledAt;

    /** When vendor/staff first responded. */
    @Column(name = "respondedAt")
    private Instant respondedAt;

    /** SLA target completion/response deadline. */
    @Column(name = "slaDueAt")
    private Instant slaDueAt;

    @Column(name = "completedAt")
    private Instant completedAt;

    /** Hours logged against this work order (staff completed widget). */
    @Column(name = "hoursLogged")
    private java.math.BigDecimal hoursLogged;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WorkOrderStatusEnum status = WorkOrderStatusEnum.SCHEDULED;

    @Column(name = "estimatedCostCents")
    private Long estimatedCostCents;

    @Column(name = "actualCostCents")
    private Long actualCostCents;

    @Column(columnDefinition = "text")
    private String notes;
}
