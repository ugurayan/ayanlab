package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.DocumentTypeEnum;
import com.ayanlabs.core.data.enums.EncryptionTypeEnum;
import com.ayanlabs.core.entity.CloudFile;
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

