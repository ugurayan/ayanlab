package com.ayanlab.pms.backend.dto.response;

import java.util.UUID;

public record OrganizationSearchResult(
    UUID organizationId,
    String name,
    String code
) {}
