package com.ayanlab.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** FedACH / reference routing number → institution name (seeded from {@code db/bank-routing-catalog.json}). */
@Entity
@Table(
    name = "BankRoutingInstitution",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(name = "UK_BankRoutingInstitution_routingNumber", columnNames = "routingNumber"),
    indexes = @Index(name = "idx_bank_routing_institution_routing", columnList = "routingNumber")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankRoutingInstitution extends BaseEntity {

    @NotBlank
    @Size(min = 9, max = 9)
    @Column(nullable = false, name = "routingNumber", length = 9)
    private String routingNumber;

    @NotBlank
    @Column(nullable = false, name = "institutionName", length = 512)
    private String institutionName;
}