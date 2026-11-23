package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.DressCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DressCodeRepository extends JpaRepository<DressCode, String> {
    List<DressCode> findByProfileId(String profileId);
    List<DressCode> findByOrgId(String orgId);
    List<DressCode> findByWorkerId(String workerId);
}
