package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.DocumentTypeEnum;
import com.ayanlab.core.data.enums.EncryptionTypeEnum;
import com.ayanlab.core.entity.CloudFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ICloudFileService {

    CloudFile upload(MultipartFile file,
                     String folder,
                     UUID entityId,
                     String entityName,
                     DocumentTypeEnum documentType,
                     Boolean approvalNeeded,
                     String approvalDescription,
                     String approvalReferenceId,
                     Boolean encrypted,
                     EncryptionTypeEnum encryptionType,
                     CoreContext context);

    String createReadSignedUrl(UUID fileId, CoreContext context);

    String createWriteSignedUrl(UUID fileId, CoreContext context);

    CloudFile approve(UUID fileId, String approvalDescription, CoreContext context);

    List<CloudFile> listByApp(CoreContext context);
}

