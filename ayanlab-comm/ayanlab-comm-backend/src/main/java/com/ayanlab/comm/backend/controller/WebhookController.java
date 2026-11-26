package com.ayanlab.comm.backend.controller;

import com.ayanlab.comm.backend.entity.DeliveryLog;
import com.ayanlab.comm.backend.entity.EmailOutbox;
import com.ayanlab.comm.backend.entity.SuppressionList;
import com.ayanlab.comm.backend.repository.DeliveryLogRepository;
import com.ayanlab.comm.backend.repository.EmailOutboxRepository;
import com.ayanlab.comm.backend.repository.SuppressionListRepository;
import com.ayanlab.comm.backend.util.IdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/public/comm/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final EmailOutboxRepository outboxRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    private final SuppressionListRepository suppressionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/delivery-events")
    public ResponseEntity<Void> handleDeliveryWebhook(@RequestBody String payloadJson) {
        log.info("Webhook Received: Handling email delivery event webhook from provider.");

        try {
            JsonNode root = objectMapper.readTree(payloadJson);
            String notificationType = root.path("eventType").asText(); // Delivery, Bounce, Complaint, Open, Click

            // Extract core metadata
            String messageId = root.path("mail").path("messageId").asText();
            String recipientEmail = root.path("mail").path("destination").path(0).asText();

            log.info("Event Type: {} for recipient: {}", notificationType, recipientEmail);

            // Fetch corresponding outbox record if matched by recipient email (simplified lookup fallback)
            EmailOutbox outbox = null;
            if (root.has("outboxId")) {
                UUID outboxId = UUID.fromString(root.path("outboxId").asText());
                outbox = outboxRepository.findById(outboxId).orElse(null);
            }

            if ("Bounce".equalsIgnoreCase(notificationType)) {
                String bounceType = root.path("bounce").path("bounceType").asText(); // Permanent (Hard) or Transient (Soft)
                log.warn("Email bounced for: {} (Type: {})", recipientEmail, bounceType);

                if ("Permanent".equalsIgnoreCase(bounceType)) {
                    // Add to Suppression List immediately to protect sender reputation
                    UUID organizationId = outbox != null ? outbox.getOrganizationId() : UUID.fromString("00000000-0000-0000-0000-000000000000"); // fallback tenant

                    if (!suppressionRepository.existsByOrganizationIdAndEmail(organizationId, recipientEmail)) {
                        SuppressionList sup = new SuppressionList();
                        sup.setOrganizationId(organizationId);
                        sup.setEmail(recipientEmail);
                        sup.setReason("HARD_BOUNCE");
                        IdGenerator.assignIfAbsent(sup);
                        suppressionRepository.save(sup);
                        log.info("Added {} to Suppression list due to Hard Bounce.", recipientEmail);
                    }
                }

                recordLog(outbox, recipientEmail, "BOUNCED", "Bounce Type: " + bounceType);

            } else if ("Complaint".equalsIgnoreCase(notificationType)) {
                log.warn("Spam complaint received from: {}", recipientEmail);

                // Add to suppression list instantly (Complaints are highly dangerous for IP Warmup & Deliverability)
                UUID organizationId = outbox != null ? outbox.getOrganizationId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

                if (!suppressionRepository.existsByOrganizationIdAndEmail(organizationId, recipientEmail)) {
                    SuppressionList sup = new SuppressionList();
                    sup.setOrganizationId(organizationId);
                    sup.setEmail(recipientEmail);
                    sup.setReason("COMPLAINT");
                    IdGenerator.assignIfAbsent(sup);
                    suppressionRepository.save(sup);
                    log.info("Added {} to Suppression list due to Spam Complaint.", recipientEmail);
                }

                recordLog(outbox, recipientEmail, "COMPLAINT", "Spam complaint reported.");

            } else if ("Delivery".equalsIgnoreCase(notificationType)) {
                log.info("Email delivered successfully to inbox for: {}", recipientEmail);
                recordLog(outbox, recipientEmail, "DELIVERED", "Confirmed delivery by webhook.");
            }

        } catch (Exception e) {
            log.error("Failed to parse delivery webhook JSON payload.", e);
        }

        return ResponseEntity.ok().build();
    }

    private void recordLog(EmailOutbox outbox, String email, String status, String details) {
        DeliveryLog logEntry = new DeliveryLog();
        if (outbox != null) {
            logEntry.setOutboxId(outbox.getId());
            logEntry.setCampaignId(outbox.getCampaignId());
        }
        logEntry.setRecipientEmail(email);
        logEntry.setStatus(status);
        logEntry.setErrorDetails(details);
        logEntry.setLoggedAt(Instant.now());

        IdGenerator.assignIfAbsent(logEntry);
        deliveryLogRepository.save(logEntry);
    }
}
