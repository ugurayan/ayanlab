package com.ayanlabs.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ayanlabs.core.config", "com.ayanlabs.security", "com.ayanlabs.booking"})
public class BookingBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingBackendApplication.class, args);
    }
}
