package com.ayanlabs.common.gcp;

import org.springframework.stereotype.Service;
import com.ayanlabs.common.config.GcpProperties;

import java.io.InputStream;

@Service
public class GoogleCloudStorageService implements GcpStorageService {
    private final GcpProperties gcpProperties;

    public GoogleCloudStorageService(GcpProperties gcpProperties) {
        this.gcpProperties = gcpProperties;
    }

    @Override
    public String uploadFile(String bucketName, String objectName, InputStream content, String contentType) {
        // Placeholder implementation: real implementation should use google-cloud-storage client.
        // Return a placeholder URI so callers can be tested without requiring the cloud client.
        return String.format("gs://%s/%s", bucketName == null ? "<bucket>" : bucketName, objectName == null ? "<object>" : objectName);
    }
}
