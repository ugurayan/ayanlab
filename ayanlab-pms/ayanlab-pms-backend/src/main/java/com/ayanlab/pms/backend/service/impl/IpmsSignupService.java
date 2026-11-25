package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.RegistrationStatusEnum;
import com.ayanlab.core.dto.request.RegisterRequest;
import com.ayanlab.core.dto.response.AccessSummary;
import com.ayanlab.core.dto.response.TokenResponse;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.service.IAccessSummaryService;
import com.ayanlab.core.service.IAuthorizationResolverService;
import com.ayanlab.core.service.IOrganizationService;
import com.ayanlab.core.service.IProfileRoleService;
import com.ayanlab.core.service.IProfileService;
import com.ayanlab.core.service.impl.UserService;
import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.core.util.PhoneNumberHelper;
import com.ayanlab.pms.backend.enums.*;
import com.ayanlab.pms.backend.dto.request.*;
import com.ayanlab.pms.backend.dto.response.*;
import com.ayanlab.pms.backend.entity.onboarding.IpmsOnboardingProfile;
import com.ayanlab.pms.backend.entity.signup.IpmsOrganizationInvite;
import com.ayanlab.pms.backend.entity.signup.IpmsOrganizationJoinRequest;
import com.ayanlab.pms.backend.entity.signup.IpmsSignupSession;
import com.ayanlab.pms.backend.repository.*;
import com.ayanlab.pms.backend.service.IIpmsSignupService;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.security.service.AuthService;
import com.ayanlab.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpmsSignupService implements IIpmsSignupService {

    private static final String ORG_ADMIN_ROLE = "ORGANIZATION_ADMINISTRATOR";
    private static final String MAINT_ORG_ADMIN_ROLE = "MAINTENANCE_ORGANIZATION";
    private static final String ORG_MEMBER_ROLE = "PROPERTY_MANAGER";
    private static final Set<String> ORG_ADMIN_ROLES = Set.of(
            "SYSTEM_ADMINISTRATOR",
            ORG_ADMIN_ROLE,
            MAINT_ORG_ADMIN_ROLE
    );

    private final AuthService authService;
    private final UserService userService;
    private final IProfileService profileService;
    private final IOrganizationService organizationService;
    private final IProfileRoleService profileRoleService;
    private final IAuthorizationResolverService authorizationResolverService;
    private final IAccessSummaryService accessSummaryService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final RoleRepository roleRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final UserTypeRepository userTypeRepository;
    private final ProfileUserTypeRepository profileUserTypeRepository;
    private final IpmsSignupSessionRepository signupSessionRepository;
    private final IpmsOnboardingProfileRepository onboardingRepository;
    private final IpmsOrganizationInviteRepository inviteRepository;
    private final IpmsOrganizationJoinRequestRepository joinRequestRepository;

    @Override
    @Transactional
    public SignupStep1Response registerStep1(SignupStep1Request request, CoreContext context) {
        UUID appId = requireAppId(context);

        if (userRepository.findByEmailAndAppId(normalizeEmail(request.email()), appId).isPresent()) {
            throw new CoreException("CONFLICT", "Email already registered");
        }
        PhoneNumberHelper.ParsedPhone parsed = PhoneNumberHelper.parseAndValidate(
                request.phone(),
                request.phoneCountryIso()
        );
        if (request.phoneCountryCode() != null && !request.phoneCountryCode().isBlank()) {
            String expectedDial = request.phoneCountryCode().trim().replace("+", "");
            if (!expectedDial.equals(parsed.countryCode())) {
                throw new CoreException("VALIDATION_ERROR", "phoneCountryCode does not match the phone number");
            }
        }
        if (userRepository.findByMobileAndAppId(parsed.e164(), appId).isPresent()) {
            throw new CoreException("CONFLICT", "Phone already registered");
        }

        RegisterRequest registerRequest = new RegisterRequest(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                parsed.e164(),
                parsed.countryIso(),
                parsed.countryCode(),
                false,
                false,
                null,
                null
        );
        TokenResponse tokens = authService.register(registerRequest, context);

        User emailCodeUser = userService.generateEmailVerificationCode(tokens.getUserId(), context);
        User mobileCodeUser = userService.generateMobileVerificationCode(tokens.getUserId(), context);

        IpmsSignupSession session = IpmsSignupSession.builder()
                .userId(tokens.getUserId())
                .profileId(tokens.getProfileId())
                .appId(appId)
                .currentStep(1)
                .status(SignupSessionStatusEnum.PENDING_EMAIL_VERIFICATION)
                .emailVerified(false)
                .mobileVerified(false)
                .build();
        session.setIsActive(true);
        IdGenerator.assignIfAbsent(session);
        session = signupSessionRepository.save(session);

        return new SignupStep1Response(
                session.getId(),
                tokens.getUserId(),
                tokens.getProfileId(),
                tokens.getAccessToken(),
                tokens.getRefreshToken(),
                true,
                true,
                emailCodeUser.getEmailConfirmationCode(),
                mobileCodeUser.getMobileVerificationCode()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SignupFieldAvailabilityResponse checkEmailAvailability(String email, CoreContext context) {
        UUID appId = requireAppId(context);
        String normalized = normalizeEmail(email);
        if (normalized.isBlank()) {
            throw new CoreException("VALIDATION_ERROR", "Email is required");
        }
        boolean available = userRepository.findByEmailAndAppId(normalized, appId).isEmpty();
        return new SignupFieldAvailabilityResponse("email", normalized, available);
    }

    @Override
    @Transactional(readOnly = true)
    public SignupFieldAvailabilityResponse checkPhoneAvailability(String phone, String phoneCountryIso, CoreContext context) {
        UUID appId = requireAppId(context);
        PhoneNumberHelper.ParsedPhone parsed = PhoneNumberHelper.parseAndValidate(phone, phoneCountryIso);
        boolean available = userRepository.findByMobileAndAppId(parsed.e164(), appId).isEmpty();
        return new SignupFieldAvailabilityResponse("phone", parsed.e164(), available);
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse verifyEmail(SignupVerifyCodeRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        UUID userId = requireUserId(context);

        boolean verified = authService.confirmEmail(userId, request.code(), context);
        if (!verified) {
            throw new CoreException("VALIDATION_ERROR", "Invalid or expired email verification code");
        }
        session.setEmailVerified(true);
        updateVerificationStatus(session);
        session = signupSessionRepository.save(session);
        return toResponse(session);
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse verifyMobile(SignupVerifyCodeRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        UUID userId = requireUserId(context);

        boolean verified = authService.confirmSms(userId, request.code(), context);
        if (!verified) {
            throw new CoreException("VALIDATION_ERROR", "Invalid or expired mobile verification code");
        }
        session.setMobileVerified(true);
        updateVerificationStatus(session);
        session = signupSessionRepository.save(session);
        return toResponse(session);
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse resendEmailVerification(CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        userService.generateEmailVerificationCode(session.getUserId(), context);
        session.setStatus(SignupSessionStatusEnum.PENDING_EMAIL_VERIFICATION);
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse resendMobileVerification(CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        userService.generateMobileVerificationCode(session.getUserId(), context);
        if (Boolean.TRUE.equals(session.getEmailVerified())) {
            session.setStatus(SignupSessionStatusEnum.PENDING_MOBILE_VERIFICATION);
        }
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public IpmsSignupSessionResponse getSession(CoreContext context) {
        return toResponse(requireSession(context));
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse selectProfileType(SignupStep2ProfileTypeRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        ensureVerificationsComplete(session);

        session.setAccountCategory(request.accountCategory());
        session.setCurrentStep(2);
        session.setStatus(SignupSessionStatusEnum.IN_PROGRESS);
        session.setPersonalAccountType(null);
        session.setOrganizationJoinMethod(null);
        session.setOrganizationCompanyType(null);
        session.setOrganizationId(null);
        session.setOnboardingProfileId(null);
        session.setJoinRequestId(null);
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse configurePersonalAccount(SignupPersonalAccountRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        ensureStep(session, 2);
        if (session.getAccountCategory() != SignupAccountCategoryEnum.PERSONAL) {
            throw new CoreException("INVALID_STATE", "Account category must be PERSONAL");
        }

        session.setPersonalAccountType(request.personalAccountType());
        session.setOrganizationJoinMethod(null);
        session.setOrganizationId(null);
        session.setCurrentStep(3);

        UserProfile profile = requireProfile(session);
        profile.setOrganizationId(null);
        profile.setProfileName(request.personalAccountType().name());
        profileService.update(profile, context);

        assignUserType(profile.getId(), mapPersonalUserTypeCode(request.personalAccountType()), context);
        assignRole(profile.getId(), mapPersonalRoleCode(request.personalAccountType()), context);
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse createNewOrganization(SignupNewOrganizationRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        ensureStep(session, 2);
        if (session.getAccountCategory() != SignupAccountCategoryEnum.ORGANIZATION) {
            throw new CoreException("INVALID_STATE", "Account category must be ORGANIZATION");
        }
        ensureWorkspaceShortCodeAvailable(request.workspaceShortCode(), null);

        User user = requireUser(session, context);
        UserProfile profile = requireProfile(session);

        Organization organization = Organization.builder()
                .name(request.companyLegalName())
                .code(normalizeShortCode(request.workspaceShortCode()))
                .description("IPMS organization: " + request.companyType().name())
                .build();
        Organization createdOrg = organizationService.create(organization, context);
        createdOrg.setIsActive(true);
        organizationRepository.save(createdOrg);

        linkOrganizationUser(session.getUserId(), createdOrg.getId(), context);

        profile.setOrganizationId(createdOrg.getId());
        profile.setProfileName("ORG_ADMIN");
        profileService.update(profile, context);
        assignRole(profile.getId(), mapOrgAdminRoleCode(request.companyType()), context);

        IpmsOnboardingProfile onboarding = IpmsOnboardingProfile.builder()
                .organizationId(createdOrg.getId())
                .adminUserId(session.getUserId())
                .adminProfileId(session.getProfileId())
                .companyLegalName(request.companyLegalName())
                .workspaceShortCode(normalizeShortCode(request.workspaceShortCode()))
                .companyEmail(request.companyEmail())
                .companyPhone(request.companyPhone())
                .website(request.website())
                .addressLine1(request.addressLine1())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .country(request.country())
                .adminFirstName(user.getFirstName())
                .adminLastName(user.getLastName())
                .adminEmail(user.getEmail())
                .adminPhone(user.getMobile())
                .companyType(request.companyType())
                .currentStep(3)
                .status(OnboardingStatusEnum.DRAFT)
                .build();
        onboarding.setIsActive(true);
        IdGenerator.assignIfAbsent(onboarding);
        onboarding = onboardingRepository.save(onboarding);

        session.setOrganizationJoinMethod(OrganizationJoinMethodEnum.NEW_COMPANY);
        session.setOrganizationCompanyType(request.companyType());
        session.setOrganizationId(createdOrg.getId());
        session.setOnboardingProfileId(onboarding.getId());
        session.setCurrentStep(3);
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse joinByInviteCode(SignupJoinByInviteRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        ensureStep(session, 2);
        if (session.getAccountCategory() != SignupAccountCategoryEnum.ORGANIZATION) {
            throw new CoreException("INVALID_STATE", "Account category must be ORGANIZATION");
        }

        IpmsOrganizationInvite invite = inviteRepository
                .findByInviteCodeAndIsActiveTrue(request.inviteCode().trim().toUpperCase())
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Invalid invite code"));

        if (invite.getExpiresAt() != null && Instant.now().isAfter(invite.getExpiresAt())) {
            throw new CoreException("VALIDATION_ERROR", "Invite code has expired");
        }
        if (invite.getUsedAt() != null) {
            throw new CoreException("VALIDATION_ERROR", "Invite code already used");
        }
        if (invite.getInvitedUserId() != null && !invite.getInvitedUserId().equals(session.getUserId())) {
            throw new CoreException("FORBIDDEN", "This invite code is assigned to another user");
        }
        if (invite.getInvitedEmail() != null) {
            User user = requireUser(session, context);
            if (!invite.getInvitedEmail().equalsIgnoreCase(user.getEmail())) {
                throw new CoreException("FORBIDDEN", "This invite code is assigned to another email");
            }
        }

        Organization org = organizationRepository.findById(invite.getOrganizationId())
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Organization not found"));

        UserProfile profile = requireProfile(session);
        profile.setOrganizationId(org.getId());
        profile.setProfileName("ORG_MEMBER");
        profileService.update(profile, context);

        linkOrganizationUser(session.getUserId(), org.getId(), context);
        String roleCode = invite.getRoleCode() != null ? invite.getRoleCode() : ORG_MEMBER_ROLE;
        assignRole(profile.getId(), roleCode, context);

        invite.setUsedAt(Instant.now());
        invite.setUsedByUserId(session.getUserId());
        inviteRepository.save(invite);

        session.setOrganizationJoinMethod(OrganizationJoinMethodEnum.INVITE_CODE);
        session.setOrganizationId(org.getId());
        session.setCurrentStep(3);
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationSearchResult> searchOrganizations(String name, CoreContext context) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return organizationRepository
                .findByNameContainingIgnoreCase(name.trim(), PageRequest.of(0, 20))
                .stream()
                .filter(org -> org.getIsActive() == null || Boolean.TRUE.equals(org.getIsActive()))
                .map(org -> new OrganizationSearchResult(org.getId(), org.getName(), org.getCode()))
                .toList();
    }

    @Override
    @Transactional
    public IpmsSignupSessionResponse requestJoinOrganization(SignupJoinRequestRequest request, CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        ensureStep(session, 2);
        if (session.getAccountCategory() != SignupAccountCategoryEnum.ORGANIZATION) {
            throw new CoreException("INVALID_STATE", "Account category must be ORGANIZATION");
        }

        Organization org = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Organization not found"));
        if (!Boolean.TRUE.equals(org.getIsActive())) {
            throw new CoreException("VALIDATION_ERROR", "Organization is not active");
        }

        joinRequestRepository.findFirstByUserIdAndOrganizationIdAndStatusAndIsActiveTrue(
                session.getUserId(), org.getId(), OrganizationJoinRequestStatusEnum.PENDING
        ).ifPresent(existing -> {
            throw new CoreException("CONFLICT", "A pending join request already exists for this organization");
        });

        IpmsOrganizationJoinRequest joinRequest = IpmsOrganizationJoinRequest.builder()
                .organizationId(org.getId())
                .userId(session.getUserId())
                .profileId(session.getProfileId())
                .message(request.message())
                .status(OrganizationJoinRequestStatusEnum.PENDING)
                .build();
        joinRequest.setIsActive(true);
        IdGenerator.assignIfAbsent(joinRequest);
        joinRequest = joinRequestRepository.save(joinRequest);

        session.setOrganizationJoinMethod(OrganizationJoinMethodEnum.SEARCH_REQUEST);
        session.setOrganizationId(org.getId());
        session.setJoinRequestId(joinRequest.getId());
        session.setCurrentStep(3);
        session.setStatus(SignupSessionStatusEnum.PENDING_JOIN_APPROVAL);
        return toResponse(signupSessionRepository.save(session));
    }

    @Override
    @Transactional
    public SignupCompleteResponse complete(CoreContext context) {
        IpmsSignupSession session = requireSession(context);
        ensureVerificationsComplete(session);

        if (session.getAccountCategory() == null) {
            throw new CoreException("INVALID_STATE", "Profile type not selected (step 2)");
        }
        if (session.getCurrentStep() == null || session.getCurrentStep() < 3) {
            throw new CoreException("INVALID_STATE", "Step 3 configuration is not complete");
        }
        if (session.getStatus() == SignupSessionStatusEnum.COMPLETED) {
            return buildCompleteResponse(session, context);
        }

        // Join-by-search: wizard can finish while waiting for org admin approval.
        if (session.getStatus() == SignupSessionStatusEnum.PENDING_JOIN_APPROVAL) {
            UserProfile profile = requireProfile(session);
            profile.setRegistrationStatus(RegistrationStatusEnum.Pending);
            profileService.update(profile, context);
            if (session.getCompletedAt() == null) {
                session.setCompletedAt(Instant.now());
                signupSessionRepository.save(session);
            }
            return buildCompleteResponse(session, context);
        }

        UserProfile profile = requireProfile(session);
        profile.setRegistrationStatus(RegistrationStatusEnum.Approved);
        profileService.update(profile, context);

        session.setStatus(SignupSessionStatusEnum.COMPLETED);
        session.setCompletedAt(Instant.now());
        session.setCurrentStep(4);
        signupSessionRepository.save(session);

        return buildCompleteResponse(session, context);
    }

    @Override
    @Transactional
    public OrganizationInviteResponse createOrganizationInvite(CreateOrganizationInviteRequest request, CoreContext context) {
        UUID orgId = requireOrgAdminContext(context);
        int expiresInDays = request.expiresInDays() != null ? request.expiresInDays() : 7;
        String roleCode = request.roleCode() != null && !request.roleCode().isBlank()
                ? request.roleCode().trim()
                : ORG_MEMBER_ROLE;
        roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(requireAppId(context), roleCode)
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Role not found: " + roleCode));

        IpmsOrganizationInvite invite = IpmsOrganizationInvite.builder()
                .organizationId(orgId)
                .inviteCode(generateInviteCode())
                .invitedEmail(request.invitedEmail())
                .invitedUserId(request.invitedUserId())
                .roleCode(roleCode)
                .expiresAt(Instant.now().plus(expiresInDays, ChronoUnit.DAYS))
                .build();
        invite.setIsActive(true);
        IdGenerator.assignIfAbsent(invite);
        invite = inviteRepository.save(invite);
        return toInviteResponse(invite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationInviteResponse> listOrganizationInvites(CoreContext context) {
        UUID orgId = requireOrgAdminContext(context);
        return inviteRepository.findByOrganizationIdAndIsActiveTrueOrderByCreatedAtDesc(orgId).stream()
                .map(this::toInviteResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationJoinRequestResponse> listOrganizationJoinRequests(
            OrganizationJoinRequestStatusEnum status,
            CoreContext context) {
        UUID orgId = requireOrgAdminContext(context);
        OrganizationJoinRequestStatusEnum resolvedStatus = status != null ? status : OrganizationJoinRequestStatusEnum.PENDING;
        return joinRequestRepository
                .findByOrganizationIdAndStatusAndIsActiveTrueOrderByCreatedAtDesc(orgId, resolvedStatus)
                .stream()
                .map(this::toJoinRequestResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrganizationJoinRequestResponse approveJoinRequest(
            UUID joinRequestId,
            ReviewJoinRequestRequest request,
            CoreContext context) {
        UUID orgId = requireOrgAdminContext(context);
        IpmsOrganizationJoinRequest joinRequest = requirePendingJoinRequest(joinRequestId, orgId);

        UserProfile profile = userProfileRepository.findById(joinRequest.getProfileId())
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Profile not found"));
        profile.setOrganizationId(orgId);
        profile.setProfileName("ORG_MEMBER");
        profile.setRegistrationStatus(RegistrationStatusEnum.Approved);
        profileService.update(profile, context);

        linkOrganizationUser(joinRequest.getUserId(), orgId, context);
        String roleCode = ORG_MEMBER_ROLE;
        assignRole(profile.getId(), roleCode, context);

        joinRequest.setStatus(OrganizationJoinRequestStatusEnum.APPROVED);
        joinRequest.setReviewedBy(context.getUserId());
        joinRequest.setReviewedAt(Instant.now());
        joinRequestRepository.save(joinRequest);

        signupSessionRepository.findByJoinRequestIdAndIsActiveTrue(joinRequestId).ifPresent(session -> {
            session.setStatus(SignupSessionStatusEnum.COMPLETED);
            session.setOrganizationJoinMethod(OrganizationJoinMethodEnum.SEARCH_REQUEST);
            session.setOrganizationId(orgId);
            session.setCurrentStep(4);
            session.setCompletedAt(Instant.now());
            signupSessionRepository.save(session);
        });

        return toJoinRequestResponse(joinRequest);
    }

    @Override
    @Transactional
    public OrganizationJoinRequestResponse rejectJoinRequest(
            UUID joinRequestId,
            ReviewJoinRequestRequest request,
            CoreContext context) {
        UUID orgId = requireOrgAdminContext(context);
        IpmsOrganizationJoinRequest joinRequest = requirePendingJoinRequest(joinRequestId, orgId);

        joinRequest.setStatus(OrganizationJoinRequestStatusEnum.REJECTED);
        joinRequest.setReviewedBy(context.getUserId());
        joinRequest.setReviewedAt(Instant.now());
        joinRequestRepository.save(joinRequest);

        signupSessionRepository.findByJoinRequestIdAndIsActiveTrue(joinRequestId).ifPresent(session -> {
            session.setStatus(SignupSessionStatusEnum.IN_PROGRESS);
            session.setOrganizationJoinMethod(null);
            session.setOrganizationId(null);
            session.setJoinRequestId(null);
            session.setCurrentStep(2);
            signupSessionRepository.save(session);
        });

        return toJoinRequestResponse(joinRequest);
    }

    private SignupCompleteResponse buildCompleteResponse(IpmsSignupSession session, CoreContext context) {
        User user = requireUser(session, context);
        UserProfile profile = requireProfile(session);

        CoreContext tokenContext = CoreContext.builder()
                .appId(session.getAppId())
                .profileId(profile.getId())
                .orgId(profile.getOrganizationId())
                .userId(user.getId())
                .clientType(context.getClientType())
                .clientPlatform(context.getClientPlatform())
                .correlationId(context.getCorrelationId())
                .language(context.getLanguage())
                .build();

        var authorization = authorizationResolverService.resolveForProfile(profile.getId(), tokenContext);
        AccessSummary access = accessSummaryService.build(authorization, profile, null);
        // Prefer signup-aware frontendRole (vendor/maint org mapping already in AccessSummary via MAINTENANCE_ORGANIZATION role)
        String frontendRole = mapFrontendRole(access.primaryRoleCode(), session);
        if (frontendRole != null && !frontendRole.equals(access.frontendRole())) {
            access = new AccessSummary(
                    access.roleCodes(),
                    access.permissionCodes(),
                    access.primaryRoleCode(),
                    frontendRole,
                    access.modules(),
                    access.menus(),
                    access.policies(),
                    access.capabilities(),
                    access.redirectTo(),
                    access.orgCode()
            );
        }

        TokenResponse tokens = TokenResponse.builder()
                .userId(user.getId())
                .appId(session.getAppId())
                .profileId(profile.getId())
                .orgId(profile.getOrganizationId())
                .accessToken(jwtService.generateAccessToken(user, tokenContext, authorization))
                .refreshToken(jwtService.generateRefreshToken(user, tokenContext))
                .access(access)
                .additionalInfo(Map.of(
                        "orgCode", access.orgCode() != null ? access.orgCode() : "",
                        "redirectTo", access.redirectTo() != null ? access.redirectTo() : "/dashboard",
                        "frontendRole", access.frontendRole() != null ? access.frontendRole() : ""
                ))
                .build();

        String redirectTo = access.redirectTo() != null ? access.redirectTo() : "/dashboard";
        return new SignupCompleteResponse(tokens, toResponse(session), redirectTo, access);
    }

    private void updateVerificationStatus(IpmsSignupSession session) {
        boolean emailDone = Boolean.TRUE.equals(session.getEmailVerified());
        boolean mobileDone = Boolean.TRUE.equals(session.getMobileVerified());
        if (emailDone && mobileDone) {
            session.setStatus(SignupSessionStatusEnum.IN_PROGRESS);
            session.setCurrentStep(1);
        } else if (emailDone) {
            session.setStatus(SignupSessionStatusEnum.PENDING_MOBILE_VERIFICATION);
        } else {
            session.setStatus(SignupSessionStatusEnum.PENDING_EMAIL_VERIFICATION);
        }
    }

    private void ensureVerificationsComplete(IpmsSignupSession session) {
        if (!Boolean.TRUE.equals(session.getEmailVerified()) || !Boolean.TRUE.equals(session.getMobileVerified())) {
            throw new CoreException("INVALID_STATE", "Email and mobile verification must be completed first");
        }
    }

    private void ensureStep(IpmsSignupSession session, int minStep) {
        ensureVerificationsComplete(session);
        if (session.getCurrentStep() == null || session.getCurrentStep() < minStep) {
            throw new CoreException("INVALID_STATE", "Complete previous signup steps first");
        }
    }

    private IpmsSignupSession requireSession(CoreContext context) {
        UUID userId = requireUserId(context);
        UUID appId = requireAppId(context);
        return signupSessionRepository.findByUserIdAndAppIdAndIsActiveTrue(userId, appId)
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Signup session not found"));
    }

    private User requireUser(IpmsSignupSession session, CoreContext context) {
        return userRepository.findById(session.getUserId())
                .orElseThrow(() -> new CoreException("NOT_FOUND", "User not found"));
    }

    private UserProfile requireProfile(IpmsSignupSession session) {
        return userProfileRepository.findById(session.getProfileId())
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Profile not found"));
    }

    private UUID requireUserId(CoreContext context) {
        if (context.getUserId() == null) {
            throw new CoreException("UNAUTHORIZED", "Authenticated user required");
        }
        return context.getUserId();
    }

    private UUID requireAppId(CoreContext context) {
        if (context.getAppId() == null) {
            throw new CoreException("VALIDATION_ERROR", "X-App-Id header is required");
        }
        return context.getAppId();
    }

    private void linkOrganizationUser(UUID userId, UUID orgId, CoreContext context) {
        if (organizationUserRepository.existsByUserIdAndOrgId(userId, orgId)) {
            return;
        }
        OrganizationUser ou = OrganizationUser.builder()
                .userId(userId)
                .orgId(orgId)
                .build();
        ou.setIsActive(true);
        IdGenerator.assignIfAbsent(ou);
        organizationUserRepository.save(ou);
    }

    private String mapOrgAdminRoleCode(OrganizationCompanyTypeEnum companyType) {
        if (companyType == OrganizationCompanyTypeEnum.VENDOR
                || companyType == OrganizationCompanyTypeEnum.MAINTENANCE) {
            return MAINT_ORG_ADMIN_ROLE;
        }
        return ORG_ADMIN_ROLE;
    }

    private void assignRole(UUID profileId, String roleCode, CoreContext context) {
        Role role = roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(requireAppId(context), roleCode)
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Role not found: " + roleCode));
        if (profileRoleRepository.existsByProfileIdAndRoleId(profileId, role.getId())) {
            return;
        }
        ProfileRole profileRole = ProfileRole.builder()
                .profileId(profileId)
                .roleId(role.getId())
                .grantedAt(Instant.now())
                .grantedBy(context.getUserId())
                .build();
        profileRole.setIsActive(true);
        profileRoleService.create(profileRole, context);
    }

    private void assignUserType(UUID profileId, String userTypeCode, CoreContext context) {
        UserType userType = userTypeRepository.findByCode(userTypeCode).orElseGet(() -> {
            UserType created = UserType.builder()
                    .code(userTypeCode)
                    .name(userTypeCode)
                    .description("IPMS signup user type")
                    .build();
            created.setIsActive(true);
            IdGenerator.assignIfAbsent(created);
            return userTypeRepository.save(created);
        });
        if (profileUserTypeRepository.existsByProfileIdAndUserTypeId(profileId, userType.getId())) {
            return;
        }
        ProfileUserType put = ProfileUserType.builder()
                .profileId(profileId)
                .userTypeId(userType.getId())
                .build();
        put.setIsActive(true);
        IdGenerator.assignIfAbsent(put);
        profileUserTypeRepository.save(put);
    }

    private String mapPersonalUserTypeCode(PersonalAccountTypeEnum type) {
        return switch (type) {
            case TENANT_RENTER -> "IPMS_TENANT_RENTER";
            case PROPERTY_OWNER -> "IPMS_PROPERTY_OWNER";
            case VENDOR -> "IPMS_VENDOR";
            case MAINTENANCE -> "IPMS_MAINTENANCE";
        };
    }

    private String mapPersonalRoleCode(PersonalAccountTypeEnum type) {
        return switch (type) {
            case TENANT_RENTER -> "TENANT";
            case PROPERTY_OWNER -> "PROPERTY_OWNER";
            case VENDOR -> "VENDOR";
            case MAINTENANCE -> "MAINTENANCE_STAFF";
        };
    }

    private String mapFrontendRole(String backendRoleCode, IpmsSignupSession session) {
        if (backendRoleCode == null) {
            return "tenant";
        }
        return switch (backendRoleCode) {
            case "SYSTEM_ADMINISTRATOR" -> "system_administrator";
            case "SYSTEM_USER", "VIEWER" -> "system_user";
            case "ORGANIZATION_ADMINISTRATOR" -> "property_management_admin";
            case "MAINTENANCE_ORGANIZATION" -> "maintenance_organization";
            case "PROPERTY_MANAGER", "ACCOUNTANT", "LEASING_AGENT" -> "property_management";
            case "TENANT", "PROPERTY_OWNER" -> "tenant";
            case "MAINTENANCE_STAFF", "VENDOR" -> "maintenance_staff";
            default -> backendRoleCode.toLowerCase();
        };
    }

    private void ensureWorkspaceShortCodeAvailable(String shortCode, UUID excludeOnboardingId) {
        String normalized = normalizeShortCode(shortCode);
        organizationRepository.findFirstByCode(normalized).ifPresent(existing -> {
            throw new CoreException("CONFLICT", "Workspace short code is already taken");
        });
        onboardingRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .filter(p -> excludeOnboardingId == null || !excludeOnboardingId.equals(p.getId()))
                .filter(p -> normalized.equalsIgnoreCase(p.getWorkspaceShortCode()))
                .findFirst()
                .ifPresent(ignored -> {
                    throw new CoreException("CONFLICT", "Workspace short code is already reserved");
                });
    }

    private String normalizeShortCode(String shortCode) {
        return shortCode.trim().toLowerCase().replaceAll("[^a-z0-9-]", "");
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        return phone == null ? "" : phone.trim();
    }

    private IpmsSignupSessionResponse toResponse(IpmsSignupSession session) {
        return new IpmsSignupSessionResponse(
                session.getId(),
                session.getUserId(),
                session.getProfileId(),
                session.getCurrentStep(),
                session.getStatus(),
                Boolean.TRUE.equals(session.getEmailVerified()),
                Boolean.TRUE.equals(session.getMobileVerified()),
                session.getAccountCategory(),
                session.getPersonalAccountType(),
                session.getOrganizationJoinMethod(),
                session.getOrganizationCompanyType(),
                session.getOrganizationId(),
                session.getOnboardingProfileId(),
                session.getJoinRequestId(),
                session.getCompletedAt(),
                resolveNextStep(session)
        );
    }

    private String resolveNextStep(IpmsSignupSession session) {
        if (session.getStatus() == SignupSessionStatusEnum.COMPLETED) {
            return "dashboard";
        }
        if (!Boolean.TRUE.equals(session.getEmailVerified())) {
            return "verify-email";
        }
        if (!Boolean.TRUE.equals(session.getMobileVerified())) {
            return "verify-mobile";
        }
        if (session.getAccountCategory() == null) {
            return "profile-type";
        }
        if (session.getAccountCategory() == SignupAccountCategoryEnum.PERSONAL && session.getPersonalAccountType() == null) {
            return "personal-account-type";
        }
        if (session.getAccountCategory() == SignupAccountCategoryEnum.ORGANIZATION && session.getOrganizationJoinMethod() == null) {
            return "organization-path";
        }
        if (session.getStatus() == SignupSessionStatusEnum.PENDING_JOIN_APPROVAL) {
            return "awaiting-join-approval";
        }
        return "complete";
    }

    private UUID requireOrgAdminContext(CoreContext context) {
        if (context.getOrgId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "Organization context (orgId) is required");
        }
        AppSecurityContext.AppContext security = AppSecurityContext.get();
        if (security == null || !security.hasAnyRole(ORG_ADMIN_ROLES.toArray(String[]::new))) {
            throw new CoreException("FORBIDDEN", "Organization administrator role required");
        }
        return context.getOrgId();
    }

    private IpmsOrganizationJoinRequest requirePendingJoinRequest(UUID joinRequestId, UUID orgId) {
        IpmsOrganizationJoinRequest joinRequest = joinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Join request not found"));
        if (!Boolean.TRUE.equals(joinRequest.getIsActive())) {
            throw new CoreException("NOT_FOUND", "Join request not found");
        }
        if (!orgId.equals(joinRequest.getOrganizationId())) {
            throw new CoreException("FORBIDDEN", "Join request belongs to a different organization");
        }
        if (joinRequest.getStatus() != OrganizationJoinRequestStatusEnum.PENDING) {
            throw new CoreException("INVALID_STATE", "Join request is not pending");
        }
        return joinRequest;
    }

    private String generateInviteCode() {
        String code;
        int attempts = 0;
        do {
            code = "INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            attempts++;
            if (attempts > 10) {
                throw new CoreException("INTERNAL_ERROR", "Could not generate unique invite code");
            }
        } while (inviteRepository.findByInviteCodeAndIsActiveTrue(code).isPresent());
        return code;
    }

    private OrganizationInviteResponse toInviteResponse(IpmsOrganizationInvite invite) {
        return new OrganizationInviteResponse(
                invite.getId(),
                invite.getOrganizationId(),
                invite.getInviteCode(),
                invite.getInvitedEmail(),
                invite.getInvitedUserId(),
                invite.getRoleCode(),
                invite.getExpiresAt(),
                invite.getCreatedAt()
        );
    }

    private OrganizationJoinRequestResponse toJoinRequestResponse(IpmsOrganizationJoinRequest joinRequest) {
        User user = userRepository.findById(joinRequest.getUserId()).orElse(null);
        return new OrganizationJoinRequestResponse(
                joinRequest.getId(),
                joinRequest.getOrganizationId(),
                joinRequest.getUserId(),
                joinRequest.getProfileId(),
                user != null ? user.getEmail() : null,
                user != null ? user.getFirstName() : null,
                user != null ? user.getLastName() : null,
                joinRequest.getMessage(),
                joinRequest.getStatus(),
                joinRequest.getCreatedAt(),
                joinRequest.getReviewedAt(),
                joinRequest.getReviewedBy()
        );
    }
}
