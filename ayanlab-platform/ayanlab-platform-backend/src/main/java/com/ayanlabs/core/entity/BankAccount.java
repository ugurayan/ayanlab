package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "BankAccount",
    schema = "core",
    indexes = @Index(name = "idx_core_bank_account_profile", columnList = "profileId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @NotBlank
    @Column(nullable = false, name = "bankName")
    private String bankName;

    @NotBlank
    @Column(nullable = false, name = "accountName")
    private String accountName;

    @Column(name = "accountNumberMasked")
    private String accountNumberMasked;

    @Column(name = "routingNumberMasked")
    private String routingNumberMasked;

    @Column(name = "currency", length = 8)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "balanceCents")
    @Builder.Default
    private Long balanceCents = 0L;

    @Column(name = "isDefault")
    @Builder.Default
    private Boolean isDefault = false;
}