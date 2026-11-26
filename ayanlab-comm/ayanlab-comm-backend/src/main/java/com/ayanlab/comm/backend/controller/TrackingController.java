package com.ayanlab.comm.backend.controller;

import com.ayanlab.comm.backend.entity.DeliveryLog;
import com.ayanlab.comm.backend.entity.EmailOutbox;
import com.ayanlab.comm.backend.repository.DeliveryLogRepository;
import com.ayanlab.comm.backend.repository.EmailOutboxRepository;
import com.ayanlab.comm.backend.util.IdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/public/comm/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final EmailOutboxRepository outboxRepository;
    private final DeliveryLogRepository deliveryLogRepository;

    // 1x1 Transparent Pixel PNG byte array
    private static final byte[] PIXEL_BYTES = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
            (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00,
            0x00, 0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
    };

    @GetMapping(value = "/open/{outboxId}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> trackOpen(
            @PathVariable("outboxId") UUID outboxId,
            HttpServletRequest request) {
        log.info("Tracking: Email opened for outbox ID: {}", outboxId);

        EmailOutbox outbox = outboxRepository.findById(outboxId).orElse(null);
        if (outbox != null) {
            DeliveryLog logEntry = new DeliveryLog();
            logEntry.setOutboxId(outbox.getId());
            logEntry.setCampaignId(outbox.getCampaignId());
            logEntry.setRecipientEmail(outbox.getRecipientEmail());
            logEntry.setStatus("OPENED");
            logEntry.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            logEntry.setIpAddress(request.getRemoteAddr());
            logEntry.setLoggedAt(Instant.now());

            IdGenerator.assignIfAbsent(logEntry);
            deliveryLogRepository.save(logEntry);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .body(PIXEL_BYTES);
    }

    @GetMapping("/click/{outboxId}")
    public void trackClick(
            @PathVariable("outboxId") UUID outboxId,
            @RequestParam("url") String redirectUrl,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        log.info("Tracking: Link clicked in outbox ID: {} -> Redirecting to: {}", outboxId, redirectUrl);

        EmailOutbox outbox = outboxRepository.findById(outboxId).orElse(null);
        if (outbox != null) {
            DeliveryLog logEntry = new DeliveryLog();
            logEntry.setOutboxId(outbox.getId());
            logEntry.setCampaignId(outbox.getCampaignId());
            logEntry.setRecipientEmail(outbox.getRecipientEmail());
            logEntry.setStatus("CLICKED");
            logEntry.setErrorDetails("Clicked URL: " + redirectUrl);
            logEntry.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            logEntry.setIpAddress(request.getRemoteAddr());
            logEntry.setLoggedAt(Instant.now());

            IdGenerator.assignIfAbsent(logEntry);
            deliveryLogRepository.save(logEntry);
        }

        response.sendRedirect(redirectUrl);
    }
}
