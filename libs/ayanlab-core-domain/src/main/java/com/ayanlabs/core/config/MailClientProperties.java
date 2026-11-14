package com.ayanlabs.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ayanlabs.mail")
public class MailClientProperties {
    private String host = "";
    private int port = 587;
    private String username = "";
    private String password = "";
}
