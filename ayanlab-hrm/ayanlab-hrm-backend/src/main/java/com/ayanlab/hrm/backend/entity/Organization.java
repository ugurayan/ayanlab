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
@Table(name = "Organization")
public class Organization {
    @Id
    private String id;
    private String name;
    private String country;
    @Column(name = "createdAt")
    private String createdAt;
}