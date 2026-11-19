package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "DebtInquiryCode", schema = "ipms", indexes = @Index(name = "idx_ipms_debt_inquiry_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtInquiryCode extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "partyName")
    private String partyName;

    @NotBlank
    @Size(min = 10, max = 10)
    @Column(nullable = false, length = 10, name = "code10")
    private String code10;

    @NotBlank
    @Column(nullable = false, name = "unitLabel")
    private String unitLabel;

    @Column(name = "expiresAt")
    private Instant expiresAt;
}