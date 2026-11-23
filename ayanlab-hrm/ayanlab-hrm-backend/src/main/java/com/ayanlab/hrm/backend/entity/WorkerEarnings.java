package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class WorkerEarnings {
    private String currency;
    private long lastMonthCents;
    private long last3MonthsCents;
    private long last6MonthsCents;
    private long last12MonthsCents;
}