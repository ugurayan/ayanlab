package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** Org department / division (tmp/auth Department). */
@Entity
@Table(name = "Department", schema = "core", indexes = @Index(name = "idx_department_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Column(nullable = false, name = "code")
    private String code;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "organizationId")
    private UUID organizationId;

    @Column(name = "appId")
    private UUID appId;
}