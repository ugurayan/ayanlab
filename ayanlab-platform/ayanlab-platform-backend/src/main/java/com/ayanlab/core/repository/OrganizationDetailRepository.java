package com.ayanlab.core.repository;

import com.ayanlab.core.entity.OrganizationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationDetailRepository extends JpaRepository<OrganizationDetail, UUID> {
    boolean existsByOrganizationIdAndDetailKey(UUID organizationId, String detailKey);
}
