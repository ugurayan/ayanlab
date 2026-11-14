package com.ayanlabs.core.service.sms;

public interface ISmsService {
    void sendSms(String phoneNumber, String message);

    void sendVerification(String phoneNumber, String message, String reference);
}
