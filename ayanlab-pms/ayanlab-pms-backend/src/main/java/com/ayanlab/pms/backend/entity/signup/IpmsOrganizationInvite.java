package com.ayanlab.pms.backend.entity.signup;

import com.ayanlab.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "IpmsOrganizationInvite",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_org_invite_code", columnList = "inviteCode", unique = true),
        @Index(name = "idx_ipms_org_invite_org", columnList = "organizationId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpmsOrganizationInvite extends BaseEntity {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "inviteCode", length = 32, unique = true)
    private String inviteCode;

    @Column(name = "invitedEmail")
    private String invitedEmail;

    @Column(name = "invitedUserId")
    private UUID invitedUserId;

    @Column(name = "roleCode", length = 64)
    private String roleCode;

    @Column(nullable = false, name = "expiresAt")
    private Instant expiresAt;

    @Column(name = "usedAt")
    private Instant usedAt;

    @Column(name = "usedByUserId")
    private UUID usedByUserId;
}
