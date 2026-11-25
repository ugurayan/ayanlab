package com.ayanlab.pms.backend.entity.files;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "StoredFile",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_file_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_file_entity", columnList = "entityType, entityId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "entityType")
    private String entityType;

    @Column(nullable = false, name = "entityId")
    private UUID entityId;

    @NotBlank
    @Column(nullable = false, name = "fileName")
    private String fileName;

    @Column(name = "contentType")
    private String contentType;

    @Column(name = "fileSizeBytes")
    private Long fileSizeBytes;

    @NotBlank
    @Column(nullable = false, name = "storagePath")
    private String storagePath;

    @Column(nullable = false, name = "uploadedByProfileId")
    private UUID uploadedByProfileId;

    public static StoredFileBuilder builder() {
        return new StoredFileBuilder();
    }

    public static class StoredFileBuilder {
        private final StoredFile v = new StoredFile();
        public StoredFileBuilder organizationId(UUID x) { v.organizationId = x; return this; }
        public StoredFileBuilder entityType(String x) { v.entityType = x; return this; }
        public StoredFileBuilder entityId(UUID x) { v.entityId = x; return this; }
        public StoredFileBuilder fileName(String x) { v.fileName = x; return this; }
        public StoredFileBuilder contentType(String x) { v.contentType = x; return this; }
        public StoredFileBuilder fileSizeBytes(Long x) { v.fileSizeBytes = x; return this; }
        public StoredFileBuilder storagePath(String x) { v.storagePath = x; return this; }
        public StoredFileBuilder uploadedByProfileId(UUID x) { v.uploadedByProfileId = x; return this; }
        public StoredFile build() { return v; }
    }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public UUID getUploadedByProfileId() { return uploadedByProfileId; }
    public void setUploadedByProfileId(UUID uploadedByProfileId) { this.uploadedByProfileId = uploadedByProfileId; }
}
