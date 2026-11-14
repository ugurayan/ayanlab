package com.ayanlabs.core.data.responses;

import lombok.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@Data @Builder
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("expiresTokenAt")
    private Long expiresTokenAt;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("expiresRefreshTokenAt")
    private Long expiresRefreshTokenAt;

    @JsonProperty("user")
    private UserDetails user;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("appId")
    private UUID appId;

    @JsonProperty("profileId")
    private UUID profileId;

    @JsonProperty("orgId")
    private UUID orgId;

    @JsonProperty("roles")
    private List<UUID> roles;

    @JsonProperty("permissions")
    private List<UUID> permissions;

    @JsonProperty("additionalInfo")
    private Map<String, Object> additionalInfo;

    @JsonProperty("access")
    private AccessSummary access;
}
