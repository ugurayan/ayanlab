package com.ayanlab.comm.backend.service;

import com.ayanlab.comm.backend.dto.CampaignCreateRequest;
import com.ayanlab.comm.backend.entity.Campaign;
import com.ayanlab.comm.backend.entity.CampaignTranslation;

import java.util.List;
import java.util.UUID;

public interface CampaignService {
    Campaign createCampaign(UUID organizationId, UUID appId, CampaignCreateRequest request);
    void autoTranslateCampaign(UUID campaignId);
    CampaignTranslation updateCampaignTranslation(UUID campaignId, String language, String subject, String bodyHtml);
    void approveCampaignTranslation(UUID campaignId, String language);
    void executeCampaign(UUID campaignId);
    Campaign getCampaignDetails(UUID campaignId);
    List<Campaign> listCampaigns(UUID organizationId);
}
