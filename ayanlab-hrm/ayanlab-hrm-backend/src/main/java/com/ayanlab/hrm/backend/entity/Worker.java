package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Worker")
public class Worker {
    @Id
    private String id;
    private String displayName;
    private double rating;
    private int ratingCount;
    private double reliability;
    private double acceptanceRate;
    private int onTimeStarts;
    private int lateStarts;
    private int jobsCompleted;
    private int jobsNotCompleted;
    private int noShows;
    private int criticalIncidents;
    private int reliabilityIncidents;
    private long avgHourlyRateCents;
    @Embedded
    private WorkerEarnings earnings;
    @Column(columnDefinition = "text")
    private String notes;

    @ElementCollection
    @CollectionTable(name = "WorkerJobProfile", joinColumns = @JoinColumn(name = "workerId"))
    @Column(name = "label")
    private List<String> jobProfiles = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "WorkerSkill", joinColumns = @JoinColumn(name = "workerId"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();
}