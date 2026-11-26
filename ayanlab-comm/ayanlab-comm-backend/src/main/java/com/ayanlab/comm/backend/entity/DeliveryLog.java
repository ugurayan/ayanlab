package com.ayanlab.comm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "delivery_logs", schema = "comm")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryLog extends BaseEntity {

    @Column(name = "outbox_id")
    private UUID outboxId;

    @Column(name = "campaign_id")
    private UUID campaignId;

    @Column(nullable = false, name = "recipient_email")
    private String recipientEmail;

    @Column(nullable = false, length = 50, name = "status")
    private String status; // DELIVERED, BOUNCED, COMPLAINT, OPENED, CLICKED, FAILED

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @Column(columnDefinition = "text", name = "error_details")
    private String errorDetails;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(length = 45, name = "ip_address")
    private String ipAddress;

    @Column(nullable = false, name = "logged_at")
    private Instant loggedAt = Instant.now();
}
