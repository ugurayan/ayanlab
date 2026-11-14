package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.files.StoredFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoredFileRepository extends IpmsOrgRepository<StoredFile, UUID> {

    List<StoredFile> findByOrganizationIdAndEntityTypeAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, String entityType);

    List<StoredFile> findByOrganizationIdAndEntityTypeAndEntityIdAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, String entityType, UUID entityId);
}
