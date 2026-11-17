package com.ayanlabs.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlabs.core.config", "com.ayanlabs.security", "com.ayanlabs.vault"})
public class VaultBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaultBackendApplication.class, args);
    }
}
