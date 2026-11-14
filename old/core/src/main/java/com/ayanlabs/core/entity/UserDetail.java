package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "UserDetail", schema = "core",
       uniqueConstraints = @UniqueConstraint(columnNames = {"profileId", "detailKey"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDetail extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "profileId", nullable = false)
//    private UserProfile profile;
    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(nullable = false, name = "detailKey")
    private String detailKey;

    @Column(nullable = false, name = "detailValue")
    private String detailValue;
}
