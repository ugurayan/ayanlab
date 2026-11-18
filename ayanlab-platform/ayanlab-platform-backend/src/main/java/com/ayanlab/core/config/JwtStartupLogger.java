package com.ayanlab.core.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JwtStartupLogger implements CommandLineRunner {

    private final JwtConfig jwtConfig;

    public JwtStartupLogger(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("[JwtStartup] \n\taccess-token-expiry-for-web=" + jwtConfig.getAccessTokenExpiryForWeb()/60/60/24 + " days"
                + "\n\taccess-token-expiry-for-mobile=" + jwtConfig.getAccessTokenExpiryForMobile()/60/60/24 + " days"
                + "\n\trefresh-token-expiry=" + jwtConfig.getRefreshTokenExpiry()/60/60/24 + " days"
                + "\n\tsecret-present=" + (jwtConfig.getSecret() != null && !jwtConfig.getSecret().isBlank()));
    }
}

