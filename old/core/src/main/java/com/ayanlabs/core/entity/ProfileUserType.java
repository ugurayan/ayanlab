package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ProfileUserType", schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"profileId", "userTypeId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileUserType extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "profileId", nullable = false)
//    private UserProfile profile;
    @Column(nullable = false, name = "profileId")
    private UUID profileId;


    @Column(nullable = false, name = "userTypeId")
    private UUID userTypeId;
}
