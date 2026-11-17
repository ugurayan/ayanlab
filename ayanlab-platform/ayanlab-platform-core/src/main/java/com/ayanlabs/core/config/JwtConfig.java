package com.ayanlabs.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ayanlabs.jwt")
public class JwtConfig {
    private String secret;
    private long accessTokenExpiryForWeb;           //  default: 1 day ( in seconds)
    private long accessTokenExpiryForMobile;        // default: 15 days ( in seconds)
    private long refreshTokenExpiry;                        // default: 60 days ( in seconds)
}
