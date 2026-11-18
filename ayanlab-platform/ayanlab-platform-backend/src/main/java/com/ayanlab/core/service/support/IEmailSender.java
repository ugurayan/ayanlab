package com.ayanlab.core.service.support;

public interface IEmailSender {
    void send(String to, String subject, String body);
}
