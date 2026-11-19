package com.ayanlab.pms.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.ayanlab.core.config",
    "com.ayanlab.core.authz",
    "com.ayanlab.security",
    "com.ayanlab.pms.backend"
})
public class PmsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PmsBackendApplication.class, args);
    }
}
