package com.ayanlab.core.service.gcp;

import com.ayanlab.core.config.GcpProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class GoogleCloudStorageService implements IGcpStorageService {

    private final GcpProperties gcpProperties;

    @Override
    public String uploadFile(String bucketName, String objectName, InputStream content, String contentType) {
        // Lightweight helper; CloudFileService remains the primary GCS integration.
        String project = gcpProperties.getProjectId() == null || gcpProperties.getProjectId().isBlank()
                ? "<project>"
                : gcpProperties.getProjectId();
        return String.format("gs://%s/%s?project=%s",
                bucketName == null ? "<bucket>" : bucketName,
                objectName == null ? "<object>" : objectName,
                project);
    }
}
