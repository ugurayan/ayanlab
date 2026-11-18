package com.ayanlab.core.service.mail;

import com.ayanlab.core.config.MailClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringMailService implements IMailService {

    private final JavaMailSender mailSender;
    private final MailClientProperties mailClientProperties;

    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        if (mailClientProperties.getUsername() != null && !mailClientProperties.getUsername().isBlank()) {
            msg.setFrom(mailClientProperties.getUsername());
        }
        mailSender.send(msg);
    }
}
