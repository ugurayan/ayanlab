package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerCertificateRepository extends JpaRepository<WorkerCertificate, String> {
    List<WorkerCertificate> findByProfileId(String profileId);
    List<WorkerCertificate> findByOrgId(String orgId);
    List<WorkerCertificate> findByWorkerId(String workerId);
}
