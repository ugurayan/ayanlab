package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ShiftTemplate")
public class ShiftTemplate extends BaseEntity {

    @Column(name = "platformOrganizationId")
    private UUID platformOrganizationId;

    @Column(name = "locationId")
    private UUID locationId;

    private String name;

    @Column(name = "staffingType")
    private String staffingType;

    @Column(name = "roleId")
    private UUID roleId;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "additionalInstructions", columnDefinition = "text")
    private String additionalInstructions;

    @Column(name = "contractTypeId")
    private UUID contractTypeId;

    private boolean recurring;

    @Column(name = "daysOfWeek")
    private String daysOfWeek;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "endTime")
    private String endTime;

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

    @Column(name = "attireRestrictionLevel")
    private String attireRestrictionLevel;

    @Column(name = "groomingLevel")
    private String groomingLevel;

    @Column(name = "staffSelectionType")
    private String staffSelectionType;

    @Transient
    private List<ShiftRequirement> requirements = new ArrayList<>();
}
