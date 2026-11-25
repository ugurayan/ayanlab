package com.ayanlab.core.dto;

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
    private String appCode;
    private UUID organizationId;

    /** {@code "{appVersion}.{profileVersion}"}; see {@code AuthzVersionChecker}. */
    private String authzVersion;

    @Builder.Default
    private Set<String> roleCodes = new LinkedHashSet<>();

    @Builder.Default
    private Set<String> permissionCodes = new LinkedHashSet<>();

    @Builder.Default
    private Set<String> policyGroupCodes = new LinkedHashSet<>();

    @Builder.Default
    private List<ResourceScopeDto> resourceScopes = new ArrayList<>();
}
