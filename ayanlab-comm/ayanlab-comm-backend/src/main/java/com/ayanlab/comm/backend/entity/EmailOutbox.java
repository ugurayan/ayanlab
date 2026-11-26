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
@Table(name = "email_outbox", schema = "comm")
@Getter
@Setter
@NoArgsConstructor
public class EmailOutbox extends BaseEntity {

    @Column(name = "campaign_id")
    private UUID campaignId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_contact_id")
    private UUID recipientContactId;

    @Column(name = "domain_id")
    private UUID domainId;

    @Column(nullable = false, name = "subject")
    private String subject;

    @Column(nullable = false, columnDefinition = "text", name = "body_html")
    private String bodyHtml;

    @Column(length = 50, name = "status")
    private String status = "PENDING"; // PENDING, PROCESSING, SENT, FAILED

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(columnDefinition = "text", name = "error_message")
    private String errorMessage;
}
