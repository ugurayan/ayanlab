package com.ayanlabs.common.mail;

public interface MailService {
    void sendSimpleEmail(String to, String subject, String body);
}

