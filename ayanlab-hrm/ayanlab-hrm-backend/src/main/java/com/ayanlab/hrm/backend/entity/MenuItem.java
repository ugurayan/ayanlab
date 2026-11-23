package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MenuItem")
public class MenuItem {

    @Id
    @Column(length = 64)
    private String code;

    private String routePath;

    @Enumerated(EnumType.STRING)
    private NavigationSection section;

    @Column(length = 64)
    private String iconKey;

    private int sortOrder;

    @Column(length = 128)
    private String requiredPermission;
}