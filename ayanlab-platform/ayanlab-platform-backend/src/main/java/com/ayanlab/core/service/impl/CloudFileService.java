package com.ayanlab.core.service.impl;

import com.ayanlab.core.config.GcpStorageProperties;
import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.enums.DocumentTypeEnum;
import com.ayanlab.core.enums.EncryptionTypeEnum;
import com.ayanlab.core.entity.CloudFile;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.CloudFileRepository;
import com.ayanlab.core.service.ICloudFileService;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class CloudFileService implements ICloudFileService {

    private final CloudFileRepository repository;
    private final GcpStorageProperties props;

    @Override
    @Transactional
    public CloudFile upload(MultipartFile file,
                            String folder,
                            UUID entityId,
                            String entityName,
                            DocumentTypeEnum documentType,
                            Boolean approvalNeeded,
                            String approvalDescription,
                            String approvalReferenceId,
                            Boolean encrypted,
                            EncryptionTypeEnum encryptionType,
                            CoreContext context) {
        if (context == null || context.getAppId() == null) {
            throw new CoreException("VALIDATION_ERROR", "AppId is required from context");
        }
        if (file == null || file.isEmpty()) {
            throw new CoreException("VALIDATION_ERROR", "File is required");
        }
        String bucket = requiredBucketName();
        String normalizedFolder = normalizeFolder(folder);
        String ext = extractExtension(file.getOriginalFilename());
        UUID cloudFileId = UUID.randomUUID();
        String cloudFileName = cloudFileId.toString();
        String objectName = normalizedFolder + cloudFileName + (ext.isBlank() ? "" : "." + ext);

        Storage storage = storageClient();
        ensureFolderExists(storage, bucket, normalizedFolder);
        try {
            BlobId blobId = BlobId.of(bucket, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            throw new CoreException("UPLOAD_FAILED", "Unable to upload file to GCS: " + e.getMessage());
        }

        CloudFile row = CloudFile.builder()
                .originalFileName(file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename())
                .cloudFileName(cloudFileName)
                .cloudFilePath(objectName)
                .entityId(entityId)
                .entityName(entityName)
                .appId(context.getAppId())
                .approvalNeeded(Boolean.TRUE.equals(approvalNeeded))
                .isApproved(false)
                .approvalDescription(approvalDescription)
                .approvalReferenceId(truncate(approvalReferenceId, 200))
                .bucketName(bucket)
                .bucketProjectId(props.getProjectId())
                .bucketFolder(normalizedFolder)
                .sizeBytes(file.getSize())
                .documentType(documentType == null ? DocumentTypeEnum.OTHER : documentType)
                .isEncrypted(Boolean.TRUE.equals(encrypted))
                .encryptionType(Boolean.TRUE.equals(encrypted) ? (encryptionType == null ? EncryptionTypeEnum.CUSTOM : encryptionType) : EncryptionTypeEnum.NONE)
                .encryptionSecret(cloudFileName)
                .contentType(file.getContentType())
                .fileExtension(ext)
                .build();
        row.setId(cloudFileId);
        row = map(row, context.getUserId(), ActionEnum.CREATE);
        return repository.save(row);
    }

    @Override
    @Transactional(readOnly = true)
    public String createReadSignedUrl(UUID fileId, CoreContext context) {
        CloudFile row = requireFile(fileId, context);
        URL signed = generateSignedUrl(row, HttpMethod.GET);
        return signed.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String createWriteSignedUrl(UUID fileId, CoreContext context) {
        CloudFile row = requireFile(fileId, context);
        URL signed = generateSignedUrl(row, HttpMethod.PUT);
        return signed.toString();
    }

    @Override
    @Transactional
    public CloudFile approve(UUID fileId, String approvalDescription, CoreContext context) {
        CloudFile row = requireFile(fileId, context);
        row.setIsApproved(true);
        row.setApprovedBy(context != null ? context.getUserId() : null);
        row.setApprovedTimeStamp(Instant.now().toEpochMilli());
        if (approvalDescription != null) {
            row.setApprovalDescription(approvalDescription);
        }
        row = map(row, context != null ? context.getUserId() : null, ActionEnum.UPDATE);
        return repository.save(row);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CloudFile> listByApp(CoreContext context) {
        if (context == null || context.getAppId() == null) {
            throw new CoreException("VALIDATION_ERROR", "AppId is required from context");
        }
        return repository.findByAppIdOrderByUpdatedAtDesc(context.getAppId());
    }

    private CloudFile requireFile(UUID fileId, CoreContext context) {
        CloudFile row = repository.findById(fileId).orElse(null);
        if (row == null) {
            throw new CoreException("NOT_FOUND", "File not found");
        }
        if (context != null && context.getAppId() != null && !context.getAppId().equals(row.getAppId())) {
            throw new CoreException("FORBIDDEN", "File belongs to a different app");
        }
        return row;
    }

    private URL generateSignedUrl(CloudFile row, HttpMethod method) {
        Storage storage = storageClient();
        BlobInfo blobInfo = BlobInfo.newBuilder(row.getBucketName(), row.getCloudFilePath()).build();
        return storage.signUrl(
                blobInfo,
                props.getSignedUrlDurationMinutes(),
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(method),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.signWith(credentials())
        );
    }

    private void ensureFolderExists(Storage storage, String bucket, String folderPrefix) {
        if (folderPrefix == null || folderPrefix.isBlank()) {
            return;
        }
        String folderObject = folderPrefix.endsWith("/") ? folderPrefix : folderPrefix + "/";
        BlobId folderBlobId = BlobId.of(bucket, folderObject);
        Blob blob = storage.get(folderBlobId);
        if (blob == null) {
            BlobInfo folderBlob = BlobInfo.newBuilder(folderBlobId).setContentType("application/x-directory").build();
            storage.create(folderBlob, new byte[0]);
        }
    }

    private String normalizeFolder(String folder) {
        String value = (folder == null || folder.isBlank()) ? "common" : folder.trim();
        value = value.replace("\\", "/");
        if (!value.endsWith("/")) {
            value = value + "/";
        }
        return value;
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }

    private String requiredBucketName() {
        if (props.getBucketName() == null || props.getBucketName().isBlank()) {
            throw new CoreException("VALIDATION_ERROR", "ayanlabs.storage.gcp.bucket-name must be configured");
        }
        return props.getBucketName();
    }

    private Storage storageClient() {
        try {
            return StorageOptions.newBuilder()
                    .setProjectId(props.getProjectId())
                    .setCredentials(credentials())
                    .build()
                    .getService();
        } catch (Exception e) {
            throw new CoreException("GCP_CLIENT_ERROR", "Failed to create GCP storage client: " + e.getMessage());
        }
    }

    private ServiceAccountCredentials credentials() {
        try {
            Resource resource = resolveCredentialsResource();
            try (InputStream in = resource.getInputStream()) {
                return ServiceAccountCredentials.fromStream(in);
            }
        } catch (IOException e) {
            throw new CoreException("GCP_AUTH_ERROR", "Failed to load service account credentials: " + e.getMessage());
        }
    }

    private Resource resolveCredentialsResource() {
        String location = props.getCredentialsResource();
        if (location == null || location.isBlank()) {
            String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (envPath != null && !envPath.isBlank()) {
                return new FileSystemResource(envPath);
            }
            throw new CoreException(
                    "GCP_CONFIG_MISSING",
                    "Set GCP_CREDENTIALS_RESOURCE or GOOGLE_APPLICATION_CREDENTIALS for GCS access");
        }
        if (location.startsWith("classpath:")) {
            return new ClassPathResource(location.substring("classpath:".length()));
        }
        return new FileSystemResource(location);
    }
}

