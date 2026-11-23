package com.ayanlab.hrm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * StaffIQ (HRM) backend — domain API under {@code /hrm/api/**}.
 * Auth/JWT: always {@code ayanlab-platform-backend} ({@code /auth/api/**}); token validation via platform-core.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.ayanlab.hrm.backend.entity")
@EnableJpaRepositories(basePackages = "com.ayanlab.hrm.backend.repository")
@ComponentScan(
        basePackages = {
            "com.ayanlab.core.config",
            "com.ayanlab.core.authz",
            "com.ayanlab.security",
            "com.ayanlab.hrm.backend"
        })
public class HrmBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmBackendApplication.class, args);
    }
}