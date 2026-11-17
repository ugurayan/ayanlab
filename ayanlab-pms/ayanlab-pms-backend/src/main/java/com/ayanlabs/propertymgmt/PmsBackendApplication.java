package com.ayanlabs.propertymgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlabs.core.config", "com.ayanlabs.security", "com.ayanlabs.propertymgmt"})
public class PmsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PmsBackendApplication.class, args);
    }
}
