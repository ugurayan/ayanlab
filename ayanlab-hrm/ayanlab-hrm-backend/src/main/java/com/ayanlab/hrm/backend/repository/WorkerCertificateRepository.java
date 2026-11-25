package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkerCertificateRepository extends JpaRepository<WorkerCertificate, UUID> {
    List<WorkerCertificate> findByProfileId(UUID profileId);
    List<WorkerCertificate> findByOrgId(UUID orgId);
    List<WorkerCertificate> findByWorkerId(UUID workerId);
}
