package com.ayanlab.pms.backend.entity.accounting;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "BankAccount",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_bank_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

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
