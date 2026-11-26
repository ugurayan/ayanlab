package com.ayanlab.comm.backend.dto;

import java.util.UUID;

public record CampaignCreateRequest(
    String name,
    UUID templateId,
    UUID targetListId,
    String deliveryMode
) {}
