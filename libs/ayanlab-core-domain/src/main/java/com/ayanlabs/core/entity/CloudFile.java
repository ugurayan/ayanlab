package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.DocumentTypeEnum;
import com.ayanlabs.core.data.enums.EncryptionTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "CloudFile", schema = "core",
        indexes = {
                @Index(name = "idx_cloud_file_app", columnList = "appId"),
                @Index(name = "idx_cloud_file_doc_type", columnList = "documentType"),
                @Index(name = "idx_cloud_file_approval_ref", columnList = "approvalReferenceId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudFile extends BaseEntity {

    @Column(nullable = false, name = "originalFileName")
    private String originalFileName;

    @Column(nullable = false, name = "cloudFileName")
    private String cloudFileName;

    @Column(nullable = false, name = "cloudFilePath")
    private String cloudFilePath;

    @Column(name = "entityId")
    private UUID entityId;

    @Column(name = "entityName")
    private String entityName;

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Builder.Default
    @Column(nullable = false, name = "approvalNeeded")
    private Boolean approvalNeeded = false;

    @Builder.Default
    @Column(nullable = false, name = "isApproved")
    private Boolean isApproved = false;

    @Column(name = "approvedBy")
    private UUID approvedBy;

    @Column(name = "approvedTimeStamp")
    private Long approvedTimeStamp;

    @Column(name = "approvalDescription", columnDefinition = "text")
    private String approvalDescription;

    @Column(name = "approvalReferenceId", length = 200)
    private String approvalReferenceId;

    @Column(nullable = false, name = "bucketName")
    private String bucketName;

    @Column(name = "bucketProjectId")
    private String bucketProjectId;

    @Column(name = "bucketFolder")
    private String bucketFolder;

    @Column(name = "sizeBytes")
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "documentType")
    private DocumentTypeEnum documentType;

    @Builder.Default
    @Column(nullable = false, name = "isEncrypted")
    private Boolean isEncrypted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "encryptionType")
    private EncryptionTypeEnum encryptionType;

    @Column(name = "encryptionSecret")
    private String encryptionSecret;

    @Column(name = "contentType")
    private String contentType;

    @Column(name = "fileExtension")
    private String fileExtension;

    @Column(name = "signedUrlRead")
    private String signedUrlRead;

    @Column(name = "signedUrlWrite")
    private String signedUrlWrite;
}

