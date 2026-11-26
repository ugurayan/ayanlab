package com.ayanlab.comm.backend.controller;

import com.ayanlab.comm.backend.dto.CampaignCreateRequest;
import com.ayanlab.comm.backend.entity.Campaign;
import com.ayanlab.comm.backend.entity.CampaignTranslation;
import com.ayanlab.comm.backend.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comm/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<Campaign> createCampaign(
            @RequestHeader("X-Organization-Id") UUID organizationId,
            @RequestHeader("X-App-Id") UUID appId,
            @RequestBody CampaignCreateRequest request) {
        return ResponseEntity.ok(campaignService.createCampaign(organizationId, appId, request));
    }

    @PostMapping("/{id}/translate")
    public ResponseEntity<Void> autoTranslate(@PathVariable("id") UUID campaignId) {
        campaignService.autoTranslateCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/translations/{lang}")
    public ResponseEntity<CampaignTranslation> updateTranslation(
            @PathVariable("id") UUID campaignId,
            @PathVariable("lang") String language,
            @RequestParam("subject") String subject,
            @RequestBody String bodyHtml) {
        return ResponseEntity.ok(campaignService.updateCampaignTranslation(campaignId, language, subject, bodyHtml));
    }

    @PostMapping("/{id}/translations/{lang}/approve")
    public ResponseEntity<Void> approveTranslation(
            @PathVariable("id") UUID campaignId,
            @PathVariable("lang") String language) {
        campaignService.approveCampaignTranslation(campaignId, language);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Void> sendCampaign(@PathVariable("id") UUID campaignId) {
        campaignService.executeCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable("id") UUID campaignId) {
        return ResponseEntity.ok(campaignService.getCampaignDetails(campaignId));
    }

    @GetMapping
    public ResponseEntity<List<Campaign>> listCampaigns(
            @RequestHeader("X-Organization-Id") UUID organizationId) {
        return ResponseEntity.ok(campaignService.listCampaigns(organizationId));
    }
}
