package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ShiftTemplate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftTemplateRepository extends JpaRepository<ShiftTemplate, UUID> {
    List<ShiftTemplate> findByPlatformOrganizationId(UUID platformOrganizationId);

    boolean existsByLocationId(UUID locationId);
}
