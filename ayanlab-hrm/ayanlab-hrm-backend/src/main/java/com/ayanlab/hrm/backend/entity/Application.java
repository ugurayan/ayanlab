package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Application")
public class Application {
    @Id
    private String id;
    private String shiftId;
    private String workerId;
    private String status;
}