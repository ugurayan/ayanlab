package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "LegalEntity")
public class LegalEntity extends BaseEntity {

    @Column(name = "platformOrganizationId")
    private UUID platformOrganizationId;

    @Column(name = "legalName")
    private String legalName;

    @Column(name = "entityType")
    private String entityType;

    @Column(name = "vatNumber")
    private String vatNumber;

    private String email;
    private String phone;
}
