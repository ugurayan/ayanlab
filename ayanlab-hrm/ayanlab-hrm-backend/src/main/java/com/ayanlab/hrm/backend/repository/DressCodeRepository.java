package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.DressCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DressCodeRepository extends JpaRepository<DressCode, UUID> {
    List<DressCode> findByProfileId(UUID profileId);
    List<DressCode> findByOrgId(UUID orgId);
    List<DressCode> findByWorkerId(UUID workerId);
}
