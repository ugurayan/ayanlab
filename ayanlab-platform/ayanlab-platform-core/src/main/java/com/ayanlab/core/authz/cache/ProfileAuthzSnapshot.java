package com.ayanlab.core.authz.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Cached profile grant list. Redis key is {@code {prefix}:{profileId}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileAuthzSnapshot(
        UUID profileId,
        List<String> permissions,
        List<String> roles,
        List<String> policyGroups,
        String authzVersion,
        Instant expiresAt) {
}
