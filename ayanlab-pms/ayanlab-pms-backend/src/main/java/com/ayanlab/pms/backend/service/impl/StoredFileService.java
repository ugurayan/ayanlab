package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.pms.backend.entity.files.StoredFile;
import com.ayanlab.pms.backend.repository.StoredFileRepository;
import com.ayanlab.pms.backend.service.IStoredFileService;
import com.ayanlab.pms.backend.service.storage.StorageProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@Transactional
public class StoredFileService extends AbstractIpmsOrgService<StoredFile, StoredFileRepository> implements IStoredFileService {

    private final StorageProvider storageProvider;

    public StoredFileService(StoredFileRepository repository, StorageProvider storageProvider) {
        super(repository);
        this.storageProvider = storageProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredFile> findAll(CoreContext context, String entityType, UUID entityId) {
        UUID orgId = requireOrgId(context);
        if (entityType != null && entityId != null) {
            return repository.findByOrganizationIdAndEntityTypeAndEntityIdAndIsActiveTrueOrderByUpdatedAtDesc(
                    orgId, entityType, entityId);
        }
        if (entityType != null) {
            return repository.findByOrganizationIdAndEntityTypeAndIsActiveTrueOrderByUpdatedAtDesc(orgId, entityType);
        }
        return findAll(context);
    }

    @Override
    @Transactional
    public StoredFile upload(CoreContext context, String entityType, UUID entityId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CoreException("VALIDATION_ERROR", "File is required");
        }
        if (file.getSize() > 15 * 1024 * 1024) {
            throw new CoreException("VALIDATION_ERROR", "File size must be <= 15MB");
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        if (!isAllowedContentType(contentType)) {
            throw new CoreException("VALIDATION_ERROR", "Unsupported content type: " + contentType);
        }
        if (context.getProfileId() == null) {
            throw new CoreException("UNAUTHORIZED", "Authenticated profile required for file upload");
        }

        String storagePath;
        try {
            storagePath = storageProvider.store(entityType, entityId.toString(), file);
        } catch (IOException e) {
            throw new CoreException("STORAGE_ERROR", "Failed to store file: " + e.getMessage());
        }

        StoredFile row = StoredFile.builder()
                .organizationId(requireOrgId(context))
                .entityType(entityType)
                .entityId(entityId)
                .fileName(file.getOriginalFilename() == null ? "uploaded-file" : file.getOriginalFilename())
                .contentType(contentType)
                .fileSizeBytes(file.getSize())
                .storagePath(storagePath)
                .uploadedByProfileId(context.getProfileId())
                .build();
        row = map(row, context.getUserId(), ActionEnum.CREATE);
        return repository.save(row);
    }

    @Override
    protected String entityLabel() {
        return "StoredFile";
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType.equalsIgnoreCase("application/pdf")
                || contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/png")
                || contentType.equalsIgnoreCase("application/msword")
                || contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
