package com.ayanlab.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gcp")
public class GcpProperties {
    private String projectId = "";
    private String credentialsPath = "";
}
