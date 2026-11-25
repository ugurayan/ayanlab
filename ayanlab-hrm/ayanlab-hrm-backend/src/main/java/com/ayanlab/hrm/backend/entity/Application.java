package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Application")
public class Application {
    @Id
    private UUID id;
    private UUID shiftId;
    private UUID workerId;
    private String status;
}