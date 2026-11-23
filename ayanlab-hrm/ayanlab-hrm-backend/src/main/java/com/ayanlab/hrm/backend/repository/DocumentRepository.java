package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, String> {
    List<Document> findByProfileId(String profileId);
    List<Document> findByOrgId(String orgId);
    List<Document> findByWorkerId(String workerId);
}
