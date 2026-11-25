package com.ayanlab.core.dto.response;

import java.util.UUID;

public record AppCatalogEntry(UUID id, String code, String name) {}