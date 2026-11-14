package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ApplicationOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationOrganizationRepository extends JpaRepository<ApplicationOrganization, UUID> {
    boolean existsByAppIdAndOrgId(UUID appId, UUID orgId);

    long countByAppIdAndIsActiveTrue(UUID appId);

    java.util.List<ApplicationOrganization> findByAppIdAndIsActiveTrue(UUID appId);
}
