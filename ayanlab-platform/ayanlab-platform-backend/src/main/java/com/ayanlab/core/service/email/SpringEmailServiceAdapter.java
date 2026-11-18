package com.ayanlab.core.service.email;

import com.ayanlab.core.service.mail.IMailService;
import com.ayanlab.core.service.support.IEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringEmailServiceAdapter implements IEmailService, IEmailSender {

    private final IMailService mailService;

    @Override
    public void sendSimple(String to, String subject, String body) {
        mailService.sendSimpleEmail(to, subject, body);
    }

    @Override
    public void send(String to, String subject, String body) {
        sendSimple(to, subject, body);
    }
}
