package com.ayanlab.pms.backend.entity.signup;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "IpmsSignupSession",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_signup_user", columnList = "userId"),
        @Index(name = "idx_ipms_signup_status", columnList = "status")
    },
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "appId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpmsSignupSession extends BaseEntity {

    @Column(nullable = false, name = "userId")
    private UUID userId;

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Builder.Default
    @Column(nullable = false, name = "currentStep")
    private Integer currentStep = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, name = "status")
    private SignupSessionStatusEnum status = SignupSessionStatusEnum.IN_PROGRESS;

    @Builder.Default
    @Column(nullable = false, name = "emailVerified")
    private Boolean emailVerified = false;

    @Builder.Default
    @Column(nullable = false, name = "mobileVerified")
    private Boolean mobileVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "accountCategory")
    private SignupAccountCategoryEnum accountCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "personalAccountType")
    private PersonalAccountTypeEnum personalAccountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "organizationJoinMethod")
    private OrganizationJoinMethodEnum organizationJoinMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "organizationCompanyType")
    private OrganizationCompanyTypeEnum organizationCompanyType;

    @Column(name = "organizationId")
    private UUID organizationId;

    @Column(name = "onboardingProfileId")
    private UUID onboardingProfileId;

    @Column(name = "joinRequestId")
    private UUID joinRequestId;

    @Column(name = "completedAt")
    private Instant completedAt;
}
