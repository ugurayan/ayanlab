package com.ayanlabs.common.email;

public interface EmailService {
    void sendSimple(String to, String subject, String body);
}

