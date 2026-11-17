package com.ayanlabs.core.service.email;

public interface IEmailService {
    void sendSimple(String to, String subject, String body);
}
