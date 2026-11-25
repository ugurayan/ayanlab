package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByProfileId(UUID profileId);
    List<Document> findByOrgId(UUID orgId);
    List<Document> findByWorkerId(UUID workerId);
}
