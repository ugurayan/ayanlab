package com.ayanlabs.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ayanlabs.storage.gcp")
public class GcpStorageProperties {
    private String projectId = "";
    private String bucketName = "";
    private String credentialsResource = "";
    private int signedUrlDurationMinutes = 30;
}

