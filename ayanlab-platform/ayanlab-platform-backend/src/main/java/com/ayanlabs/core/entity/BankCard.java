package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "BankCard",
    schema = "core",
    indexes = @Index(name = "idx_core_bank_card_profile", columnList = "profileId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCard extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @NotBlank
    @Column(nullable = false, name = "cardholderName")
    private String cardholderName;

    @Column(name = "brand", length = 32)
    private String brand;

    @Column(name = "lastFour", length = 4)
    private String lastFour;

    @Column(name = "expiryMonth")
    private Integer expiryMonth;

    @Column(name = "expiryYear")
    private Integer expiryYear;

    @Column(name = "tokenRef")
    private String tokenRef;

    @Column(name = "isDefault")
    @Builder.Default
    private Boolean isDefault = false;
}