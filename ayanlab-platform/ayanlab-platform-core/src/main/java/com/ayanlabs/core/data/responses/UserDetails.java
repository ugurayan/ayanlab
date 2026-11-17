package com.ayanlabs.core.data.responses;

import lombok.*;

import java.time.Instant;
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
public class UserDetails {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @JsonProperty("allProfiles")
    private List<UUID> allProfiles;

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("registrationStatus")
    private String registrationStatus;

    @JsonProperty("approvalStatus")
    private Boolean approvalStatus;

    @JsonProperty("approvedAt")
    private Instant approvedAt;

    @JsonProperty("additionalInfo")
    private Map<String, Object> additionalInfo;
}
