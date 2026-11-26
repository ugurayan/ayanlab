package com.ayanlab.comm.backend.service;

import java.util.UUID;

public interface EmailSenderService {
    /**
     * Dispatches rendered email payload via configured SMTP or AWS SES.
     * Returns provider-specific message ID on success.
     */
    String sendEmail(String from, String to, String subject, String bodyHtml, UUID domainId);
}
