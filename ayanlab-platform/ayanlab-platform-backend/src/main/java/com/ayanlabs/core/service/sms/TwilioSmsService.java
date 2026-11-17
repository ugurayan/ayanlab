package com.ayanlabs.core.service.sms;

import com.ayanlabs.core.config.TwilioProperties;
import com.ayanlabs.core.exception.CoreException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwilioSmsService implements ISmsService {

    private final TwilioProperties twilioProperties;
    private volatile boolean initialized;

    @PostConstruct
    void init() {
        if (!StringUtils.hasText(twilioProperties.getAccountSid())
                || !StringUtils.hasText(twilioProperties.getAuthToken())
                || !StringUtils.hasText(twilioProperties.getFromNumber())) {
            log.warn("Twilio credentials incomplete — SMS will fail until configured");
            return;
        }
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        initialized = true;
        log.info("Twilio SMS initialized (from={})", twilioProperties.getFromNumber());
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        if (!initialized) {
            throw new CoreException("SMS_NOT_CONFIGURED", "Twilio is not configured");
        }
        if (!StringUtils.hasText(phoneNumber)) {
            throw new CoreException("VALIDATION_ERROR", "Phone number is required for SMS");
        }
        try {
            Message sent = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioProperties.getFromNumber()),
                    message
            ).create();
            log.info("Twilio SMS sent sid={} to={} status={}", sent.getSid(), phoneNumber, sent.getStatus());
        } catch (Exception e) {
            log.error("Twilio SMS failed to={}: {}", phoneNumber, e.getMessage());
            throw new CoreException("SMS_SEND_FAILED", "Failed to send SMS: " + e.getMessage());
        }
    }

    @Override
    public void sendVerification(String phoneNumber, String message, String reference) {
        sendSms(phoneNumber, message);
    }
}
