package com.ayanlabs.common.sms;

public interface SmsService {
    void sendSms(String phoneNumber, String message);

    // Send a verification message with a reference id (profile id or similar)
    void sendVerification(String phoneNumber, String message, String reference);
}
