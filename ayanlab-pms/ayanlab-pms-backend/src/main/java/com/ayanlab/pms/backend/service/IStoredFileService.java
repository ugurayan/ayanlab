package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.pms.backend.entity.files.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IStoredFileService extends IIpmsOrgScopedService<StoredFile> {

    List<StoredFile> findAll(CoreContext context, String entityType, UUID entityId);

    StoredFile upload(CoreContext context, String entityType, UUID entityId, MultipartFile file);
}
