package com.ayanlab.comm.backend.service.impl;

import com.ayanlab.comm.backend.service.EmailSenderService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Override
    public String sendEmail(String from, String to, String subject, String bodyHtml, UUID domainId) {
        log.info("Dispatching email from: {} to: {} (Subject: {})", from, to, subject);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);

            mailSender.send(message);

            String mockMessageId = "msg_" + UUID.randomUUID().toString().replace("-", "");
            log.info("Email successfully sent to: {}. MessageId: {}", to, mockMessageId);
            return mockMessageId;

        } catch (Exception e) {
            log.error("SMTP Delivery failed for recipient: {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Email delivery failed: " + e.getMessage(), e);
        }
    }
}
