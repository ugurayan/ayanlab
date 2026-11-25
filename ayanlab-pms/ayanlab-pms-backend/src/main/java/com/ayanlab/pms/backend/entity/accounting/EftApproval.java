package com.ayanlab.pms.backend.entity.accounting;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "EftApproval",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_eftapproval_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EftApproval extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "reference")
    private String reference;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;
}
