package com.ayanlab.vault.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlab.core.config", "com.ayanlab.core.authz", "com.ayanlab.security", "com.ayanlab.vault.backend"})
public class VaultBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaultBackendApplication.class, args);
    }
}
