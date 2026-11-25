package com.ayanlab.security.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import com.ayanlab.core.enums.RegistrationStatusEnum;
import com.ayanlab.core.dto.request.EnrollRequest;
import com.ayanlab.core.dto.request.LoginRequest;
import com.ayanlab.core.dto.request.RefreshTokenRequest;
import com.ayanlab.core.dto.request.RegisterRequest;
import com.ayanlab.core.dto.response.AccessSummary;
import com.ayanlab.core.dto.response.TokenResponse;
import com.ayanlab.core.dto.response.UserDetails;
import com.ayanlab.core.exception.*;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.service.IAccessSummaryService;
import com.ayanlab.core.service.IAuthorizationResolverService;
import com.ayanlab.core.service.EmailDispatchService;
import com.ayanlab.core.dto.request.SendTemplateEmailRequest;
import com.ayanlab.core.service.IIpLogService;
import com.ayanlab.core.service.ILoginHistoryService;
import com.ayanlab.core.service.impl.ProfileService;
import com.ayanlab.core.service.impl.UserService;
import com.ayanlab.core.service.INotificationTemplateService;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.enums.ProfileTenancyEnum;
import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.core.util.PhoneNumberHelper;
import com.ayanlab.core.util.ProfileSlugHelper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AppRepository appRepository;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final INotificationTemplateService notificationTemplateService;
    private final IAuthorizationResolverService authorizationResolverService;
    private final IAccessSummaryService accessSummaryService;
    private final EmailDispatchService emailDispatchService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final RoleRepository roleRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final UserTypeRepository userTypeRepository;
    private final ILoginHistoryService loginHistoryService;
    private final IIpLogService ipLogService;

    @Transactional
    public TokenResponse register(RegisterRequest req, CoreContext context) {
        UUID appId = context.getAppId();
        if (appId == null)
            throw new CoreException("VALIDATION_ERROR", "Header x-app-id is required");

        PhoneNumberHelper.ParsedPhone parsed = PhoneNumberHelper.parseAndValidate(
                req.phone(),
                req.phoneCountryIso()
        );

        User user = User.builder()
                .appId(appId)
                .email(req.email())
                .isEmailVerified(false)
                .password(passwordEncoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .mobile(parsed.e164())
                .mobileCountryCode(parsed.countryCode())
                .isMobileVerified(false)
                .build();

        user = userService.create(user, context);

        UserProfile profile = UserProfile.builder()
                .userId(user.getId())
                .appId(appId)
                .profileSlug(ProfileSlugHelper.defaultSlug(context.getOrgId()))
                .tenancy(context.getOrgId() != null ? ProfileTenancyEnum.ORGANIZATION : ProfileTenancyEnum.PERSONAL)
                .organizationId(context.getOrgId())
                .registeredAt(Instant.now())
                .registrationStatus(RegistrationStatusEnum.Created)
                .build();
        profile = profileService.create(profile, context);

        boolean sendEmail = Boolean.TRUE.equals(req.sendEmail());
        boolean sendSms = Boolean.TRUE.equals(req.sendSms());
        if (sendEmail && req.emailTemplateCode() != null) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("firstName", user.getFirstName());
                params.put("lastName", user.getLastName());
                params.put("email", user.getEmail());
                params.put("appId", appId.toString());
                SendTemplateEmailRequest emailRequest = new SendTemplateEmailRequest(
                        user.getEmail(),
                        appId,
                        req.emailTemplateCode(),
                        context.getLanguage(),
                        params,
                        true
                );
                emailDispatchService.sendTemplateEmail(emailRequest, context);
            } catch (Exception ex) {
                log.warn("[register] email template not found or error sending email: {}", ex.getMessage());
            }
        }
        if (sendSms && req.smsTemplateCode() != null) {
            try {
                NotificationTemplate tmpl = notificationTemplateService.findById(
                    notificationTemplateService.findAll(0,1, appId, com.ayanlab.core.enums.NotificationTypeEnum.SMS, req.smsTemplateCode(), context).getContent().stream().findFirst().map(NotificationTemplate::getId).orElse(null), context);
                log.info("[register] sending sms using template={} to={} correlationId={}", req.smsTemplateCode(), user.getMobile(), context.getCorrelationId());
            } catch (Exception ex) {
                log.warn("[register] sms template not found or error sending sms: {}", ex.getMessage());
            }
        }

        Map<String, Object> additional = new HashMap<>();
        additional.put("coreContext", context);

        CoreContext tokenContext = CoreContext.builder()
            .appId(appId)
            .profileId(profile.getId())
            .orgId(profile.getOrganizationId())
            .userId(user.getId())
            .clientType(context.getClientType())
            .clientPlatform(context.getClientPlatform())
            .correlationId(context.getCorrelationId())
            .build();

        EffectiveAuthorization authorization = authorizationResolverService.resolveForProfile(profile.getId(), tokenContext);
        AccessSummary access = null;
        try {
            access = accessSummaryService.build(authorization, profile, null);
        } catch (Exception ex) {
            log.warn("[register] access summary failed (non-fatal): {}", ex.getMessage(), ex);
        }

        return TokenResponse.builder()
                .userId(user.getId())
                .appId(appId)
                .profileId(profile.getId())
                .orgId(profile.getOrganizationId())
                .accessToken(jwtService.generateAccessToken(user, tokenContext, authorization))
                .refreshToken(jwtService.generateRefreshToken(user, tokenContext))
                .user(toUserDetails(user, profile))
                .access(access)
                .additionalInfo(additional)
                .build();
    }

    @Transactional
    public TokenResponse login(LoginRequest req, CoreContext context) {
        if ((req.getEmail() == null || req.getEmail().isBlank()) && (req.getPhone() == null || req.getPhone().isBlank())) {
            throw new BadRequestException("Email or phone is required");
        }

        UUID appId = resolveAppIdForLogin(req, context);

        User user = resolveLoginUser(req, appId);
        if (user.getPassword() == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            loginHistoryService.record(user.getId(), appId, null, false, "INVALID_CREDENTIALS", context);
            ipLogService.record("login", false, context);
            throw new BadRequestException("Invalid email or password");
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            loginHistoryService.record(user.getId(), appId, null, false, "INACTIVE", context);
            ipLogService.record("login", false, context);
            throw new ForbiddenException("User account is inactive");
        }

        UserProfile profile = resolveProfileForLogin(user.getId(), appId, req.getProfileId(), req.getProfileSlug());

        // Local/dev convenience: org-scoped IPMS CRUD needs oid in JWT.
        // If the profile has no organization yet, attach DEMO_ORG when present.
        ensureOrganizationForLogin(profile, appId);

        if (req.getOrgId() != null) {
            if (profile.getOrganizationId() == null || !req.getOrgId().equals(profile.getOrganizationId())) {
                throw new ForbiddenException("User is not a member of the requested organization");
            }
        }

        CoreContext tokenContext = CoreContext.builder()
            .appId(appId)
            .profileId(profile.getId())
            .orgId(profile.getOrganizationId())
            .userId(user.getId())
            .clientType(context.getClientType())
            .clientPlatform(context.getClientPlatform())
            .correlationId(context.getCorrelationId())
            .language(context.getLanguage())
            .build();

        EffectiveAuthorization authorization = authorizationResolverService.resolveForProfile(profile.getId(), tokenContext);
        AccessSummary access = accessSummaryService.build(authorization, profile, null);

        Map<String, Object> additional = new HashMap<>();
        additional.put("orgCode", access.orgCode());
        additional.put("redirectTo", access.redirectTo());
        additional.put("frontendRole", access.frontendRole());

        loginHistoryService.record(user.getId(), appId, profile.getId(), true, null, tokenContext);
        ipLogService.record("login", true, tokenContext);

        return TokenResponse.builder()
                .userId(user.getId())
            .appId(appId)
            .profileId(profile.getId())
            .orgId(profile.getOrganizationId())
            .accessToken(jwtService.generateAccessToken(user, tokenContext, authorization))
            .refreshToken(jwtService.generateRefreshToken(user, tokenContext))
            .user(toUserDetails(user, profile))
            .access(access)
            .additionalInfo(additional)
            .build();
    }

    private User resolveLoginUser(LoginRequest req, UUID appId) {
        Optional<User> userOpt = Optional.empty();
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            userOpt = userRepository.findByEmailAndAppId(req.getEmail().trim().toLowerCase(), appId);
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByEmailAndAppId(req.getEmail().trim(), appId);
            }
        }
        if (userOpt.isEmpty() && req.getPhone() != null && !req.getPhone().isBlank()) {
            userOpt = userRepository.findByMobileAndAppId(req.getPhone().trim(), appId);
        }
        return userOpt.orElseThrow(() -> new BadRequestException("Invalid email or password"));
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest req, CoreContext context) {
        if (req == null || req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }
        try {
            CoreContext fromToken = jwtService.buildCoreContextFromToken(req.getRefreshToken());
            UUID userId = fromToken.getUserId();
            UUID appId = fromToken.getAppId() != null ? fromToken.getAppId() : context.getAppId();
            if (userId == null || appId == null) {
                throw new BadRequestException("Invalid refresh token");
            }
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
            UUID profileId = fromToken.getProfileId();
            UserProfile profile;
            if (profileId != null) {
                profile = userProfileRepository.findById(profileId)
                    .orElseThrow(() -> new BadRequestException("Profile not found"));
                if (!userId.equals(profile.getUserId()) || !appId.equals(profile.getAppId())) {
                    throw new BadRequestException("Invalid refresh token");
                }
            } else {
                profile = resolveProfileForLogin(userId, appId, null, null);
            }

            ensureOrganizationForLogin(profile, appId);

            CoreContext tokenContext = CoreContext.builder()
                .appId(appId)
                .profileId(profile.getId())
                .orgId(profile.getOrganizationId())
                .userId(userId)
                .clientType(context.getClientType() != null ? context.getClientType() : fromToken.getClientType())
                .clientPlatform(context.getClientPlatform())
                .correlationId(context.getCorrelationId())
                .build();

            EffectiveAuthorization authorization = authorizationResolverService.resolveForProfile(profile.getId(), tokenContext);
            AccessSummary access = accessSummaryService.build(authorization, profile, null);
            Map<String, String> tokens = jwtService.refreshTokensIfValid(
                req.getRefreshToken(),
                user,
                tokenContext,
                Set.of(),
                Set.of()
            );
            // regenerate with authorization codes
            String accessToken = jwtService.generateAccessToken(user, tokenContext, authorization);
            String refreshToken = tokens.get("refreshToken");

            return TokenResponse.builder()
                .userId(userId)
                .appId(appId)
                .profileId(profile.getId())
                .orgId(profile.getOrganizationId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserDetails(user, profile))
                .access(access)
                .additionalInfo(Map.of(
                    "orgCode", access.orgCode() != null ? access.orgCode() : "",
                    "redirectTo", access.redirectTo() != null ? access.redirectTo() : "/dashboard"
                ))
                .build();
        } catch (JwtException ex) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
    }

    @Transactional
    public void logout(String token, CoreContext context) {
        // Stateless JWT: client discards tokens. Hook for future blacklist.
        log.info("[logout] userId={} correlationId={}", context.getUserId(), context.getCorrelationId());
    }

    @Transactional
    public TokenResponse enrollToApp(EnrollRequest req, CoreContext context) {
        App targetApp = appRepository.findByCode(req.getAppKey())
            .orElseThrow(() -> new ResourceNotFoundException("App", "code", req.getAppKey()));
        UUID targetAppId = targetApp.getId();

        UUID userId = req.getUserId() != null ? req.getUserId() : context.getUserId();
        if (userId == null) {
            throw new CoreException("VALIDATION_ERROR", "userId required (body or authenticated context)");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        if (req.getEmail() == null || !req.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new BadRequestException("Email does not match user record");
        }

        UUID orgId = req.getOrgId();
        if (orgId != null) {
            organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId.toString()));
        }

        String slug = req.getProfileSlug();
        if (slug == null || slug.isBlank()) {
            slug = ProfileSlugHelper.defaultSlug(orgId, req.getRoleKey());
        }

        if (userProfileRepository.findByUserIdAndAppIdAndProfileSlugAndIsActiveTrue(userId, targetAppId, slug).isPresent()) {
            throw new CoreException("CONFLICT", "Profile already exists: " + slug);
        }

        UUID userTypeId = null;
        if (req.getUserTypeCode() != null && !req.getUserTypeCode().isBlank()) {
            userTypeId = userTypeRepository.findByCode(req.getUserTypeCode()).map(UserType::getId).orElse(null);
        }

        UserProfile profile = UserProfile.builder()
            .userId(userId)
            .appId(targetAppId)
            .profileSlug(slug)
            .profileName(req.getProfileName())
            .organizationId(orgId)
            .tenancy(orgId != null ? ProfileTenancyEnum.ORGANIZATION : ProfileTenancyEnum.PERSONAL)
            .primaryUserTypeId(userTypeId)
            .registrationStatus(RegistrationStatusEnum.Created)
            .registeredAt(Instant.now())
            .build();
        IdGenerator.assignIfAbsent(profile);
        profile = userProfileRepository.save(profile);
        final UUID enrolledProfileId = profile.getId();

        if (req.getRoleKey() != null && !req.getRoleKey().isBlank()) {
            roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(targetAppId, req.getRoleKey()).ifPresent(role -> {
                ProfileRole pr = ProfileRole.builder()
                    .profileId(enrolledProfileId)
                    .roleId(role.getId())
                    .grantedAt(Instant.now())
                    .build();
                IdGenerator.assignIfAbsent(pr);
                profileRoleRepository.save(pr);
            });
        }

        if (orgId != null) {
            linkUserToOrganization(userId, orgId);
        }

        return issueTokenResponse(user, profile, context);
    }

    /**
     * List active profiles for a user in the app from {@code x-app-id} (or optional override).
     */
    public List<UserProfile> listProfilesForUser(UUID userId, UUID appId) {
        if (userId == null || appId == null) {
            throw new CoreException("VALIDATION_ERROR", "userId and appId required");
        }
        return userProfileRepository.findByUserIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(userId, appId);
    }

    private UserProfile resolveProfileForLogin(UUID userId, UUID appId, UUID profileId, String profileSlug) {
        if (profileId != null) {
            UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException("Profile not found"));
            if (!userId.equals(profile.getUserId()) || !appId.equals(profile.getAppId())) {
                throw new ForbiddenException("Profile does not match user or app");
            }
            if (Boolean.FALSE.equals(profile.getIsActive())) {
                throw new BadRequestException("Profile is inactive");
            }
            return profile;
        }
        if (profileSlug != null && !profileSlug.isBlank()) {
            return userProfileRepository.findByUserIdAndAppIdAndProfileSlugAndIsActiveTrue(userId, appId, profileSlug)
                .orElseThrow(() -> new BadRequestException("No profile for slug: " + profileSlug));
        }
        List<UserProfile> profiles = userProfileRepository.findByUserIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(userId, appId);
        if (profiles.isEmpty()) {
            throw new BadRequestException("No profile found for this application — use POST /auth/api/enroll");
        }
        if (profiles.size() > 1) {
            throw new BadRequestException(
                "Multiple profiles in this app; pass profileSlug or profileId on login");
        }
        return profiles.get(0);
    }

    private void linkUserToOrganization(UUID userId, UUID orgId) {
        boolean linked = organizationUserRepository.findAll().stream()
            .anyMatch(ou -> userId.equals(ou.getUserId()) && orgId.equals(ou.getOrgId()));
        if (!linked) {
            OrganizationUser ou = OrganizationUser.builder().userId(userId).orgId(orgId).build();
            IdGenerator.assignIfAbsent(ou);
            organizationUserRepository.save(ou);
        }
    }

    private TokenResponse issueTokenResponse(User user, UserProfile profile, CoreContext context) {
        CoreContext tokenContext = CoreContext.builder()
            .appId(profile.getAppId())
            .profileId(profile.getId())
            .orgId(profile.getOrganizationId())
            .userId(user.getId())
            .clientType(context.getClientType())
            .clientPlatform(context.getClientPlatform())
            .correlationId(context.getCorrelationId())
            .language(context.getLanguage())
            .build();

        EffectiveAuthorization authorization = authorizationResolverService.resolveForProfile(profile.getId(), tokenContext);
        AccessSummary access = accessSummaryService.build(authorization, profile, null);

        Map<String, Object> additional = new HashMap<>();
        additional.put("orgCode", access != null ? access.orgCode() : null);
        additional.put("redirectTo", access != null ? access.redirectTo() : "/dashboard");
        additional.put("profileSlug", profile.getProfileSlug());

        return TokenResponse.builder()
            .userId(user.getId())
            .appId(profile.getAppId())
            .profileId(profile.getId())
            .orgId(profile.getOrganizationId())
            .accessToken(jwtService.generateAccessToken(user, tokenContext, authorization))
            .refreshToken(jwtService.generateRefreshToken(user, tokenContext))
            .user(toUserDetails(user, profile))
            .access(access)
            .additionalInfo(additional)
            .build();
    }

    public Object createOrganization(Object req) {
        throw new BadRequestException("Not yet implemented");
    }

    public Object inviteToOrg(UUID orgId, Object req) {
        throw new BadRequestException("Not yet implemented");
    }

    @Transactional
    public boolean confirmEmail(UUID userId, String code, CoreContext context) {
        return userService.validateEmailCode(userId, code, context);
    }

    @Transactional
    public boolean confirmSms(UUID userId, String code, CoreContext context) {
        return userService.validateMobileCode(userId, code, context);
    }

    @Transactional
    public String forgotPassword(String emailOrMobile, CoreContext context) {
        UUID appId = context.getAppId();
        if (appId == null) throw new CoreException("VALIDATION_ERROR", "Header x-app-id required");
        return userService.initiatePasswordReset(emailOrMobile, appId, context);
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword, CoreContext context) {
        return userService.resetPassword(token, newPassword, context);
    }

    @Transactional
    public boolean forgotUsername(String emailOrMobile, CoreContext context) {
        UUID appId = context.getAppId();
        if (appId == null) throw new CoreException("VALIDATION_ERROR", "Header x-app-id required");
        return userService.sendUsernameReminder(emailOrMobile, appId, context);
    }

    @Transactional
    public boolean changePassword(UUID userId, String oldPassword, String newPassword, CoreContext context) {
        return userService.changePassword(userId, oldPassword, newPassword, context);
    }

    private UserDetails toUserDetails(User user, UserProfile profile) {
        return UserDetails.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .mobile(user.getMobile())
            .organizationId(profile.getOrganizationId())
            .registrationStatus(profile.getRegistrationStatus() != null ? profile.getRegistrationStatus().name() : null)
            .build();
    }

    /**
     * Org-scoped property-mgmt APIs require JWT {@code oid}. For local/dev profiles that never completed
     * org join, attach {@code DEMO_ORG} so CRUD pages work after login.
     */
    private UUID resolveAppIdForLogin(LoginRequest req, CoreContext context) {
        if (StringUtils.hasText(req.getAppKey())) {
            return appRepository.findByCode(req.getAppKey().trim())
                .map(App::getId)
                .orElseThrow(() -> new ResourceNotFoundException("App", "code", req.getAppKey()));
        }
        UUID appId = context.getAppId();
        if (appId == null) {
            throw new CoreException("VALIDATION_ERROR", "App context is required (X-App-Id or appKey)");
        }
        appRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("App", "id", appId.toString()));
        return appId;
    }

    private void ensureOrganizationForLogin(UserProfile profile, UUID appId) {
        if (profile.getOrganizationId() != null) {
            return;
        }
        Optional<Organization> demoOrg = organizationRepository.findFirstByCode("DEMO_ORG");
        if (demoOrg.isEmpty()) {
            log.warn("[login] profile {} has no organizationId and DEMO_ORG is missing", profile.getId());
            return;
        }
        UUID orgId = demoOrg.get().getId();
        profile.setOrganizationId(orgId);
        userProfileRepository.save(profile);
        if (!organizationUserRepository.existsByUserIdAndOrgId(profile.getUserId(), orgId)) {
            OrganizationUser ou = OrganizationUser.builder()
                    .userId(profile.getUserId())
                    .orgId(orgId)
                    .build();
            IdGenerator.assignIfAbsent(ou);
            ou.markNew();
            organizationUserRepository.save(ou);
        }
        log.info("[login] Attached profile {} to DEMO_ORG for org-scoped API access", profile.getId());
    }
}
