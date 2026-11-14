package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "ApplicationModule",
        schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "moduleId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationModule extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "moduleId", nullable = false)
//    private Module module;
    @Column(nullable = false, name = "moduleId")
    private UUID moduleId;

}
