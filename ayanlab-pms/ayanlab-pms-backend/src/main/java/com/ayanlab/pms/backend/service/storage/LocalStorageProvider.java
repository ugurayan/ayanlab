package com.ayanlab.pms.backend.service.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalStorageProvider implements StorageProvider {

    @Override
    public String store(String entityType, String entityId, MultipartFile file) throws IOException {
        String baseDir = System.getProperty("java.io.tmpdir") + "/ayanlabs/ipms/uploads";
        Path dir = Path.of(baseDir, safe(entityType), safe(entityId));
        Files.createDirectories(dir);
        String targetName = UUID.randomUUID() + "_" + safe(file.getOriginalFilename());
        Path destination = dir.resolve(targetName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
