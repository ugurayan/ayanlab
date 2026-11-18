package com.ayanlab.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "ApplicationUserType",
        schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "userTypeId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationUserType extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userTypeId", nullable = false)
//    private UserType userType;
    @Column(nullable = false, name = "userTypeId")
    private UUID userTypeId;

}
