package com.ayanlab.comm.backend.worker;

import com.ayanlab.comm.backend.entity.DeliveryLog;
import com.ayanlab.comm.backend.entity.EmailOutbox;
import com.ayanlab.comm.backend.event.CommEmailDispatchEvent;
import com.ayanlab.comm.backend.repository.DeliveryLogRepository;
import com.ayanlab.comm.backend.repository.EmailOutboxRepository;
import com.ayanlab.comm.backend.service.EmailSenderService;
import com.ayanlab.comm.backend.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailDispatchWorker {

    private final EmailOutboxRepository outboxRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    private final EmailSenderService emailSenderService;

    private static final int MAX_RETRIES = 3;

    @KafkaListener(topics = "comm-email-dispatch", groupId = "ayanlabs-comm-group")
    @Transactional
    public void consumeDispatchEvent(CommEmailDispatchEvent event) {
        log.info("Kafka Event Received: Processing outbox email ID: {}", event.outboxId());

        EmailOutbox outbox = outboxRepository.findById(event.outboxId()).orElse(null);
        if (outbox == null) {
            log.error("Outbox record not found for ID: {}", event.outboxId());
            return;
        }

        if (!"PENDING".equals(outbox.getStatus()) && !"PROCESSING".equals(outbox.getStatus())) {
            log.warn("Outbox record {} has status {}. Skipping execution.", outbox.getId(), outbox.getStatus());
            return;
        }

        outbox.setStatus("PROCESSING");
        outboxRepository.save(outbox);

        try {
            // Programmatic default sender from verified or platform settings
            String fromEmail = "bulletin@ayanlab.com"; // Customized fallback
            String providerMsgId = emailSenderService.sendEmail(
                    fromEmail,
                    outbox.getRecipientEmail(),
                    outbox.getSubject(),
                    outbox.getBodyHtml(),
                    outbox.getDomainId()
            );

            // Successfully dispatched
            outbox.setStatus("SENT");
            outboxRepository.save(outbox);

            // Record delivery success log
            DeliveryLog deliveryLog = new DeliveryLog();
            deliveryLog.setOutboxId(outbox.getId());
            deliveryLog.setCampaignId(outbox.getCampaignId());
            deliveryLog.setRecipientEmail(outbox.getRecipientEmail());
            deliveryLog.setStatus("DELIVERED");
            deliveryLog.setProviderMessageId(providerMsgId);
            deliveryLog.setLoggedAt(Instant.now());

            IdGenerator.assignIfAbsent(deliveryLog);
            deliveryLogRepository.save(deliveryLog);

            log.info("Successfully delivered outbox mail ID: {} to {}", outbox.getId(), outbox.getRecipientEmail());

        } catch (Exception e) {
            log.error("Error occurred while delivering outbox email ID: {}", outbox.getId(), e);

            int retryCount = outbox.getRetryCount() + 1;
            outbox.setRetryCount(retryCount);

            if (retryCount >= MAX_RETRIES) {
                outbox.setStatus("FAILED");
                outbox.setErrorMessage("Max retries exceeded. First error: " + e.getMessage());
                outboxRepository.save(outbox);

                // Log final delivery failure
                DeliveryLog failLog = new DeliveryLog();
                failLog.setOutboxId(outbox.getId());
                failLog.setCampaignId(outbox.getCampaignId());
                failLog.setRecipientEmail(outbox.getRecipientEmail());
                failLog.setStatus("FAILED");
                failLog.setErrorDetails(e.getMessage());
                failLog.setLoggedAt(Instant.now());

                IdGenerator.assignIfAbsent(failLog);
                deliveryLogRepository.save(failLog);

                log.error("Outbox email ID: {} marked as FAILED permanently.", outbox.getId());
            } else {
                // Exponential backoff retry scheduling
                long backoffMinutes = (long) Math.pow(2, retryCount); // 2, 4, 8 minutes backoff
                outbox.setStatus("PENDING"); // Put back to PENDING for scheduler to pick up if Kafka event fails
                outbox.setNextAttemptAt(Instant.now().plus(backoffMinutes, ChronoUnit.MINUTES));
                outbox.setErrorMessage(e.getMessage());
                outboxRepository.save(outbox);

                log.warn("Scheduled outbox email ID: {} for retry in {} minutes. (Retry Count: {})",
                        outbox.getId(), backoffMinutes, retryCount);
            }
        }
    }
}
