package com.ayanlab.core.data;

import com.ayanlab.core.data.enums.ClientPlatformEnum;
import com.ayanlab.core.data.enums.ClientTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreContext {

    @JsonProperty("correlationId")
    private UUID correlationId;

    @NotNull
    @JsonProperty("appId")
    private UUID appId;

    @JsonProperty("clientType")
    @Builder.Default
    private ClientTypeEnum clientType = ClientTypeEnum.Web;


    @JsonProperty("clientPlatform")
    @Builder.Default
    private ClientPlatformEnum clientPlatform = ClientPlatformEnum.Web;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("profileId")
    private UUID profileId;

    @JsonProperty("orgId")
    private UUID orgId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("refreshToken")
    private String refreshToken;

    /** From {@code X-Client-Ip} (request tracing / logging). */
    @JsonProperty("clientIp")
    private String clientIp;

    /** From {@code X-Client-Language} (e.g. tr, en). */
    @JsonProperty("language")
    private String language;
}
