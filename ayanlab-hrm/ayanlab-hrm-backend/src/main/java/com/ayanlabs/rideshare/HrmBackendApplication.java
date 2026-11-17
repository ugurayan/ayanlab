package com.ayanlabs.rideshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlabs.core.config", "com.ayanlabs.security", "com.ayanlabs.rideshare"})
public class HrmBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmBackendApplication.class, args);
    }
}
