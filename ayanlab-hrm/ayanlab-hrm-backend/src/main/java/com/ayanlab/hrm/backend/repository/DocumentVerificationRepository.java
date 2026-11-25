package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.DocumentVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DocumentVerificationRepository extends JpaRepository<DocumentVerification, UUID> {
    List<DocumentVerification> findByProfileId(UUID profileId);
    List<DocumentVerification> findByOrgId(UUID orgId);
    List<DocumentVerification> findByWorkerId(UUID workerId);
}
