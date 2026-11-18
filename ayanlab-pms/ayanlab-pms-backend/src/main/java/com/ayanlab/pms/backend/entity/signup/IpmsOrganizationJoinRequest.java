package com.ayanlab.pms.backend.entity.signup;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.enums.OrganizationJoinRequestStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "IpmsOrganizationJoinRequest",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_join_req_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_join_req_user", columnList = "userId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpmsOrganizationJoinRequest extends BaseEntity {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "userId")
    private UUID userId;

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(name = "message", length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, name = "status")
    private OrganizationJoinRequestStatusEnum status = OrganizationJoinRequestStatusEnum.PENDING;

    @Column(name = "reviewedBy")
    private UUID reviewedBy;

    @Column(name = "reviewedAt")
    private Instant reviewedAt;
}
