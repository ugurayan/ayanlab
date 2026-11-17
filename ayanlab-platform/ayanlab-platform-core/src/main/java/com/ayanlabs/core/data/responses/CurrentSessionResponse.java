package com.ayanlabs.core.data.responses;

import com.ayanlabs.core.data.dto.EffectiveAuthorization;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CurrentSessionResponse {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private UUID appId;
    private UUID profileId;
    private UUID orgId;
    private List<ProfileSummary> profiles;
    private EffectiveAuthorization authorization;

    @Data
    @Builder
    public static class ProfileSummary {
        private UUID id;
        private String profileSlug;
        private UUID organizationId;
        private Boolean isActive;
    }
}
