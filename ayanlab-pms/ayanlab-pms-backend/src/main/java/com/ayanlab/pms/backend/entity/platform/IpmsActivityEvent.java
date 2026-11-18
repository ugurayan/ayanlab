package com.ayanlab.pms.backend.entity.platform;

import com.ayanlab.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Cross-org / platform activity feed entries for system dashboard widgets.
 */
@Entity
@Table(
    name = "IpmsActivityEvent",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_activity_app", columnList = "appId"),
        @Index(name = "idx_ipms_activity_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_activity_occurred", columnList = "occurredAt")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpmsActivityEvent extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "eventType")
    private String eventType;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String detail;

    @Column(name = "actorProfileId")
    private UUID actorProfileId;

    @Column(name = "actorUserId")
    private UUID actorUserId;

    @Column(nullable = false, name = "occurredAt")
    private Instant occurredAt;
}
