package com.ayanlabs.propertymgmt.data.responses;

import java.util.UUID;

public record OrganizationSearchResult(
    UUID organizationId,
    String name,
    String code
) {}
