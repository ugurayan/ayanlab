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
@Table(name = "WorkerIncident")
public class WorkerIncident {
    @Id
    private UUID id;
    private UUID workerId;
    private String type;
    private String severity;
    private String occurredAt;
    private String summary;
}