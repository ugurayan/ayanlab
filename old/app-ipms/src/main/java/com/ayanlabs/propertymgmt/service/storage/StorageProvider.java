package com.ayanlabs.propertymgmt.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageProvider {

    String store(String entityType, String entityId, MultipartFile file) throws IOException;
}
