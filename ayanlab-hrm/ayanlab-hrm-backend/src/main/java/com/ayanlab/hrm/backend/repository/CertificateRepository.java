package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    List<Certificate> findByProfileId(UUID profileId);
    List<Certificate> findByOrgId(UUID orgId);
    List<Certificate> findByWorkerId(UUID workerId);
}
