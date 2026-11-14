package com.ayanlabs.core.service.mail;

public interface IMailService {
    void sendSimpleEmail(String to, String subject, String body);
}
