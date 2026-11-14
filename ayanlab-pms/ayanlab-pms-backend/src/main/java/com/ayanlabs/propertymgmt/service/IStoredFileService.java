package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.propertymgmt.entity.files.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IStoredFileService extends IIpmsOrgScopedService<StoredFile> {

    List<StoredFile> findAll(CoreContext context, String entityType, UUID entityId);

    StoredFile upload(CoreContext context, String entityType, UUID entityId, MultipartFile file);
}
