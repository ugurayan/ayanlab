package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByOrganizationId(UUID organizationId);
    List<Campaign> findByStatus(String status);
}
