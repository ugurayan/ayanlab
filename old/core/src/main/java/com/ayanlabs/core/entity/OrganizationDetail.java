package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Key/value attributes scoped to an {@link Organization} (shared pattern with {@link UserDetail} for profiles).
 */
@Entity
@Table(
    name = "OrganizationDetail",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "detailKey"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDetail extends BaseEntity {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "detailKey")
    private String detailKey;

    @Column(nullable = false, name = "detailValue", columnDefinition = "text")
    private String detailValue;
}
