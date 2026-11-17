package com.ayanlabs.comm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlabs.core.config", "com.ayanlabs.security", "com.ayanlabs.comm"})
public class CommBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommBackendApplication.class, args);
    }
}
