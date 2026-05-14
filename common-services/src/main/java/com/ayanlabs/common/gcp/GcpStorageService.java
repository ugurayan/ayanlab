package com.ayanlabs.common.gcp;

import java.io.InputStream;

public interface GcpStorageService {
    String uploadFile(String bucketName, String objectName, InputStream content, String contentType);
}

