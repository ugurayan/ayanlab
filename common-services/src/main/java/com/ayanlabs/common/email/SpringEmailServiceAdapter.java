package com.ayanlabs.common.email;

import org.springframework.stereotype.Service;
import com.ayanlabs.common.mail.MailService;

@Service
public class SpringEmailServiceAdapter implements EmailService {
    private final MailService mailService;

    public SpringEmailServiceAdapter(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void sendSimple(String to, String subject, String body) {
        mailService.sendSimpleEmail(to, subject, body);
    }
}

