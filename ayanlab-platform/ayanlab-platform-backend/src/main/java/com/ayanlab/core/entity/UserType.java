package com.ayanlab.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserType", schema = "core")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;
}

