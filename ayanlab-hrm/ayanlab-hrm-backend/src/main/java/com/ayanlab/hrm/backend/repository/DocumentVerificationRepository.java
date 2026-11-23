package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.DocumentVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVerificationRepository extends JpaRepository<DocumentVerification, String> {
    List<DocumentVerification> findByProfileId(String profileId);
    List<DocumentVerification> findByOrgId(String orgId);
    List<DocumentVerification> findByWorkerId(String workerId);
}
