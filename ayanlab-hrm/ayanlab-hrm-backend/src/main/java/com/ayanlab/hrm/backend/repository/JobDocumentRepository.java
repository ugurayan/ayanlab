package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobDocumentRepository extends JpaRepository<JobDocument, UUID> {
    List<JobDocument> findByProfileId(UUID profileId);
    List<JobDocument> findByOrgId(UUID orgId);
    List<JobDocument> findByWorkerId(UUID workerId);
}
