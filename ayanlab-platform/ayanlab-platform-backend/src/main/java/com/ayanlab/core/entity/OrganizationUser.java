package com.ayanlab.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "OrganizationUser", schema = "core", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "orgId"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationUser extends BaseEntity {

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userId", nullable = false)
//    private User user;
    @Column(nullable = false, name = "userId")
    private UUID userId;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "orgId", nullable = false)
//    private Organization organization;
    @Column(nullable = false, name = "orgId")
    private UUID orgId;

}
