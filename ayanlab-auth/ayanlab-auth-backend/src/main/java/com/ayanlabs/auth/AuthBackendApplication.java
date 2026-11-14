package com.ayanlabs.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.ayanlabs.core", "com.ayanlabs.security", "com.ayanlabs.auth"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.ayanlabs\\.core\\.controller\\..*"))
@EntityScan(basePackages = "com.ayanlabs")
@EnableJpaRepositories(basePackages = "com.ayanlabs")
public class AuthBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthBackendApplication.class, args);
    }
}