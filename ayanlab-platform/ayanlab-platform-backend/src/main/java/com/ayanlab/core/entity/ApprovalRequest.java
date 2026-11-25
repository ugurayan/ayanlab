package com.ayanlab.core.entity;

import com.ayanlab.core.enums.ApprovalStatusEnum;
import com.ayanlab.core.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.enums.ApprovalTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "ApprovalRequest",
    schema = "core",
    indexes = {
        @Index(name = "idx_approval_org", columnList = "organizationId"),
        @Index(name = "idx_approval_subject", columnList = "subjectType, subjectId"),
        @Index(name = "idx_approval_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "subjectType")
    private ApprovalSubjectTypeEnum subjectType;

    @Column(nullable = false, name = "subjectId")
    private UUID subjectId;

    @Column(name = "organizationId")
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "approvalType")
    private ApprovalTypeEnum approvalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    @Builder.Default
    private ApprovalStatusEnum status = ApprovalStatusEnum.PENDING;

    @Column(name = "requestedByUserId")
    private UUID requestedByUserId;

    @Column(name = "requestedAt")
    private Instant requestedAt;

    @Column(name = "decidedByUserId")
    private UUID decidedByUserId;

    @Column(name = "decidedAt")
    private Instant decidedAt;

    @Column(name = "decisionNote", length = 2000)
    private String decisionNote;
}
