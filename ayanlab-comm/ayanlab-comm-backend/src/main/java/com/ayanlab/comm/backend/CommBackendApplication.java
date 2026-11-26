package com.ayanlab.comm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlab.core.config", "com.ayanlab.core.authz", "com.ayanlab.security", "com.ayanlab.comm.backend"})
@EnableScheduling
@EnableAsync
public class CommBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommBackendApplication.class, args);
    }
}
