package com.ayanlabs.core.service.gcp;

import java.io.InputStream;

public interface IGcpStorageService {
    String uploadFile(String bucketName, String objectName, InputStream content, String contentType);
}
