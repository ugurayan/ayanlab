package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.CampaignTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampaignTranslationRepository extends JpaRepository<CampaignTranslation, UUID> {
    Optional<CampaignTranslation> findByCampaignIdAndLanguage(UUID campaignId, String language);
    List<CampaignTranslation> findByCampaignId(UUID campaignId);
}
