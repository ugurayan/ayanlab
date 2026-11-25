package com.ayanlab.core.provider;

import com.ayanlab.core.dto.ResourceScopeDto;
import com.ayanlab.core.enums.ResourceScopeTypeEnum;

import java.util.*;

/** Compact JWT representation of resource scopes: {@code [{"t": TYPE, "id": uuid, "org": uuid|null}]}. */
public final class ResourceScopeClaims {

    public static final String CLAIM = "scopes";

    private ResourceScopeClaims() {}

    public static List<Map<String, String>> toClaim(Collection<ResourceScopeDto> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return List.of();
        }
        List<Map<String, String>> out = new ArrayList<>();
        for (ResourceScopeDto scope : scopes) {
            if (scope == null || scope.getResourceType() == null || scope.getResourceId() == null) {
                continue;
            }
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("t", scope.getResourceType().name());
            entry.put("id", scope.getResourceId().toString());
            if (scope.getOrganizationId() != null) {
                entry.put("org", scope.getOrganizationId().toString());
            }
            out.add(entry);
        }
        return out;
    }

    public static List<ResourceScopeDto> fromClaim(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<ResourceScopeDto> out = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            try {
                ResourceScopeTypeEnum type = ResourceScopeTypeEnum.valueOf(String.valueOf(map.get("t")));
                UUID id = UUID.fromString(String.valueOf(map.get("id")));
                Object org = map.get("org");
                out.add(ResourceScopeDto.builder()
                    .resourceType(type)
                    .resourceId(id)
                    .organizationId(org != null ? UUID.fromString(String.valueOf(org)) : null)
                    .build());
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries rather than widening access
            }
        }
        return out;
    }
}
