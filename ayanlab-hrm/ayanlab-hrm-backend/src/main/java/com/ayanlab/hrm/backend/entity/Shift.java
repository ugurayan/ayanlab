package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Shift")
public class Shift extends BaseEntity {

    @Column(name = "platformOrganizationId")
    private UUID platformOrganizationId;

    @Column(name = "templateId")
    private UUID templateId;

    @Column(name = "locationId")
    private UUID locationId;

    @Column(name = "roleId")
    private UUID roleId;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "additionalInstructions", columnDefinition = "text")
    private String additionalInstructions;

    @Column(name = "startsAt")
    private Instant startsAt;

    @Column(name = "endsAt")
    private Instant endsAt;

    @Column(name = "unpaidBreakMinutes")
    private Integer unpaidBreakMinutes;

    @Column(name = "wageType")
    private String wageType;

    @Column(name = "wageAmountMinor")
    private Integer wageAmountMinor;

    private String currency;

    @Column(name = "tipPercent")
    private BigDecimal tipPercent;

    @Column(name = "tipAmountMinor")
    private Integer tipAmountMinor;

    @Column(name = "employeesNeeded")
    private Integer employeesNeeded;

    @Column(name = "dressCodeId")
    private UUID dressCodeId;

    @Column(name = "staffSelectionType")
    private String staffSelectionType;

    private String status;

    @Transient
    private List<ShiftRequirement> requirements = new ArrayList<>();
}
