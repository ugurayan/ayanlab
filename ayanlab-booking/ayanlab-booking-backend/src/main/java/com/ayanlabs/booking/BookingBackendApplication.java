package com.ayanlabs.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.ayanlabs.core", "com.ayanlabs.security", "com.ayanlabs.booking"},
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.REGEX,
                pattern = "com\\.ayanlabs\\.core\\.controller\\..*"))
@EntityScan(basePackages = "com.ayanlabs")
@EnableJpaRepositories(basePackages = "com.ayanlabs")
public class BookingBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingBackendApplication.class, args);
    }
}
