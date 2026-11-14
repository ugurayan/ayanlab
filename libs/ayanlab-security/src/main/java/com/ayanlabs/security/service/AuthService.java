package com.ayanlabs.security.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.dto.EffectiveAuthorization;
import com.ayanlabs.core.data.enums.RegistrationStatusEnum;
import com.ayanlabs.core.data.requests.EnrollRequest;
import com.ayanlabs.core.data.requests.LoginRequest;
import com.ayanlabs.core.data.requests.RefreshTokenRequest;
import com.ayanlabs.core.data.requests.RegisterRequest;
import com.ayanlabs.core.data.responses.AccessSummary;
import com.ayanlabs.core.data.responses.TokenResponse;
import com.ayanlabs.core.data.responses.UserDetails;
import com.ayanlabs.core.exception.*;
import com.ayanlabs.core.entity.*;
import com.ayanlabs.core.repository.*;
import com.ayanlabs.core.service.IAccessSummaryService;
import com.ayanlabs.core.service.IAuthorizationResolverService;
import com.ayanlabs.core.service.EmailDispatchService;
import com.ayanlabs.core.data.requests.SendTemplateEmailRequest;
import com.ayanlabs.core.service.impl.ProfileService;
import com.ayanlabs.core.service.impl.UserService;
import com.ayanlabs.core.service.INotificationTemplateService;
import com.ayanlabs.core.entity.NotificationTemplate;
import com.ayanlabs.core.util.IdGenerator;
import com.ayanlabs.core.util.PhoneNumberHelper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    notificationTemplateService.findAll(0,1, appId, com.ayanlabs.core.data.enums.NotificationTypeEnum.SMS, req.smsTemplateCode(), context).getContent().stream().findFirst().map(NotificationTemplate::getId).orElse(null), context);
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

        UUID appId = context.getAppId();
        if (appId == null) {
            throw new CoreException("VALIDATION_ERROR", "Header AppId App context is required");
        }

        appRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("App", "id", appId.toString()));

        User user = resolveLoginUser(req, appId);
        if (user.getPassword() == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new ForbiddenException("User account is inactive");
        }

        UserProfile profile = userProfileRepository.findByUserIdAndAppId(user.getId(), appId)
            .orElseThrow(() -> new BadRequestException("No profile found for this application"));

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
            UserProfile profile = userProfileRepository.findByUserIdAndAppId(userId, appId)
                .orElseThrow(() -> new BadRequestException("No profile found for this application"));

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
    public Object enrollToApp(EnrollRequest req, CoreContext context) {
        UUID appId = context.getAppId();
        if (appId == null) throw new CoreException("VALIDATION_ERROR", "Header AppId App context is required");
        throw new BadRequestException("Not yet implemented");
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
