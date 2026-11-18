package com.ayanlab.core.service.email;

public interface IEmailService {
    void sendSimple(String to, String subject, String body);
}
