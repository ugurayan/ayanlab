package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ApplicationOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationOrganizationRepository extends JpaRepository<ApplicationOrganization, UUID> {
    boolean existsByAppIdAndOrgId(UUID appId, UUID orgId);

    long countByAppIdAndIsActiveTrue(UUID appId);

    List<ApplicationOrganization> findByIsActiveTrue();

    List<ApplicationOrganization> findByAppId(UUID appId);

    List<ApplicationOrganization> findByAppIdAndIsActiveTrue(UUID appId);
}
