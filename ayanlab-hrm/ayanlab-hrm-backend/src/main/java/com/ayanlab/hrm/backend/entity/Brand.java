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
@Table(name = "Brand")
public class Brand extends BaseEntity {

    @Column(name = "platformOrganizationId")
    private UUID platformOrganizationId;

    private String name;
    private String area;
}
