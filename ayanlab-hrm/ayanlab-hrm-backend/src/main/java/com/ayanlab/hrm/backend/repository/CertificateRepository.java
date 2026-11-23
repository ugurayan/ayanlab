package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, String> {
    List<Certificate> findByProfileId(String profileId);
    List<Certificate> findByOrgId(String orgId);
    List<Certificate> findByWorkerId(String workerId);
}
