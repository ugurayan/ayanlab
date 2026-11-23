package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDocumentRepository extends JpaRepository<JobDocument, String> {
    List<JobDocument> findByProfileId(String profileId);
    List<JobDocument> findByOrgId(String orgId);
    List<JobDocument> findByWorkerId(String workerId);
}
