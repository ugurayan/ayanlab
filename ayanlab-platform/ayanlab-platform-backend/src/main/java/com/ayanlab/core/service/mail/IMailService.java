package com.ayanlab.core.service.mail;

public interface IMailService {
    void sendSimpleEmail(String to, String subject, String body);
}
