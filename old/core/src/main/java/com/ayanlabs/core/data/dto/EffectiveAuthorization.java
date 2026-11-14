package com.ayanlabs.core.data.dto;

import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EffectiveAuthorization {

    private UUID profileId;
    private UUID appId;
    private UUID organizationId;

    @Builder.Default
    private Set<String> roleCodes = new LinkedHashSet<>();

    @Builder.Default
    private Set<String> permissionCodes = new LinkedHashSet<>();

    @Builder.Default
    private Set<String> policyGroupCodes = new LinkedHashSet<>();

    @Builder.Default
    private List<ResourceScopeDto> resourceScopes = new ArrayList<>();
}
