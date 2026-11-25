package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.LegalEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalEntityRepository extends JpaRepository<LegalEntity, UUID> {
    List<LegalEntity> findByPlatformOrganizationId(UUID platformOrganizationId);
}
