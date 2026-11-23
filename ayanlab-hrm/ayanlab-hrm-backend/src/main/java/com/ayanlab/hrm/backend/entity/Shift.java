package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Shift")
public class Shift {
    @Id
    private String id;
    private String orgId;
    private String locationId;
    private String role;
    private String startAt;
    private String endAt;
    private int headcount;
    private String status;
}