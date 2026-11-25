package com.ayanlab.pms.backend.entity.settings;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** Org-level WhatsApp Business API config stub (B0.7). Secrets stored as opaque stub until Comm service. */
@Entity
@Table(
    name = "OrgWhatsappChannelSetting",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_whatsapp_org", columnList = "organizationId", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgWhatsappChannelSetting extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    @Column(name = "businessAccountId")
    private String businessAccountId;

    @Column(name = "phoneNumberId")
    private String phoneNumberId;

    /** Placeholder — replace with vault reference in production. */
    @Column(name = "apiTokenStub")
    private String apiTokenStub;

    @Column(name = "webhookVerifyToken")
    private String webhookVerifyToken;
}