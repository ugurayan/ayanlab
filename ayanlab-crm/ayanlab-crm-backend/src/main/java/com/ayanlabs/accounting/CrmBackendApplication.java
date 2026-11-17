package com.ayanlabs.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlabs.core.config", "com.ayanlabs.security", "com.ayanlabs.accounting"})
public class CrmBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrmBackendApplication.class, args);
    }
}
