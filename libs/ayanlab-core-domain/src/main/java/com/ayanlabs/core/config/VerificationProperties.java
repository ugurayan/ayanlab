package com.ayanlabs.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ayanlabs.verification")
public class VerificationProperties {

    /** Email verification code TTL in minutes (default 30). */
    private int emailCodeExpirationMinutes = 30;

    /** Mobile/SMS verification code TTL in minutes (default 30). */
    private int mobileCodeExpirationMinutes = 30;
}
