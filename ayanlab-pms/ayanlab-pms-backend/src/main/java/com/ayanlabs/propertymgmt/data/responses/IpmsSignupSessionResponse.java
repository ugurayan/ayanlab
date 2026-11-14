package com.ayanlabs.propertymgmt.data.responses;

import com.ayanlabs.propertymgmt.data.enums.*;

import java.time.Instant;
import java.util.UUID;

public record IpmsSignupSessionResponse(
    UUID signupSessionId,
    UUID userId,
    UUID profileId,
    Integer currentStep,
    SignupSessionStatusEnum status,
    boolean emailVerified,
    boolean mobileVerified,
    SignupAccountCategoryEnum accountCategory,
    PersonalAccountTypeEnum personalAccountType,
    OrganizationJoinMethodEnum organizationJoinMethod,
    OrganizationCompanyTypeEnum organizationCompanyType,
    UUID organizationId,
    UUID onboardingProfileId,
    UUID joinRequestId,
    Instant completedAt,
    String nextStep
) {}
