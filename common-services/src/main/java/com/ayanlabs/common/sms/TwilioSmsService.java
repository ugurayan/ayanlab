package com.ayanlabs.common.sms;

import org.springframework.stereotype.Service;
import com.ayanlabs.common.config.TwilioProperties;

@Service
public class TwilioSmsService implements SmsService {
    private final TwilioProperties twilioProperties;

    public TwilioSmsService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        // Placeholder: here you'd initialize Twilio client using twilioProperties.getAccountSid() and getAuthToken()
        System.out.println("Sending SMS from " + twilioProperties.getFromNumber() + " to " + phoneNumber + ": " + message);
    }

    @Override
    public void sendVerification(String phoneNumber, String message, String reference) {
        // For verification we might include the reference in the message or track it separately.
        sendSms(phoneNumber, message + " (ref:" + reference + ")");
    }
}
