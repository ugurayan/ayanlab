package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Organization;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByPlatformOrganizationId(UUID platformOrganizationId);
}
