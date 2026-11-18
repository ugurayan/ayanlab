package com.ayanlab.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "ApplicationOrganization",
        schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "orgId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationOrganization extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "orgId", nullable = false)
//    private Organization organization;
    @Column(nullable = false, name = "orgId")
    private UUID orgId;
}
