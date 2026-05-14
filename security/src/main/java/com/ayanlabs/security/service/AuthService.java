package com.ayanlabs.security.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.RegistrationStatusEnum;
import com.ayanlabs.core.data.requests.EnrollRequest;
import com.ayanlabs.core.data.requests.LoginRequest;
import com.ayanlabs.core.data.requests.RefreshTokenRequest;
import com.ayanlabs.core.data.requests.RegisterRequest;
import com.ayanlabs.core.data.responses.TokenResponse;
import com.ayanlabs.core.exception.*;
import com.ayanlabs.core.entity.*;
import com.ayanlabs.core.repository.*;
import com.ayanlabs.core.service.impl.ProfileService;
import com.ayanlabs.core.service.impl.UserService;
import com.ayanlabs.core.service.INotificationTemplateService;
import com.ayanlabs.core.entity.NotificationTemplate;
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
    private final RoleRepository roleRepository;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final INotificationTemplateService notificationTemplateService;


    @Transactional
    public TokenResponse register(RegisterRequest req, CoreContext context) {
       // Before application should be already validated

        UUID appId = context.getAppId();
        if (appId == null)
            throw new CoreException("VALIDATION_ERROR", "Header x-app-id is required");

        User user = User.builder()
                .appId(appId)
                .email(req.email())
                .isEmailVerified(false)
                .password(passwordEncoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .mobile(req.phone())
                .isMobileVerified(false)
                .build();

        user = userService.create(user, context);

        // As a  default userProfile will be created with PENDING status. The userProfile will be VERIFIED if autoVerify is true, else it will be PENDING and caller can trigger verification using common-services if desired
        UserProfile profile = UserProfile.builder()
                .userId(user.getId())
                .appId(appId)
                .organizationId(context.getOrgId())
                .registeredAt(Instant.now())
                .registrationStatus(RegistrationStatusEnum.Created)
                .build();
        profile = profileService.create(profile, context);

        // handle notification templates if requested
        boolean sendEmail = Boolean.TRUE.equals(req.sendEmail());
        boolean sendSms = Boolean.TRUE.equals(req.sendSms());
        if (sendEmail && req.emailTemplateCode() != null) {
            try {
                NotificationTemplate tmpl = notificationTemplateService.findById(
                    notificationTemplateService.findAll(0,1, appId, com.ayanlabs.core.data.enums.NotificationTypeEnum.EMAIL, req.emailTemplateCode(), context).getContent().stream().findFirst().map(NotificationTemplate::getId).orElse(null), context);
                // In real implementation, send email using mail service with tmpl.subject and tmpl.body
                // For now just log
                log.info("[register] sending email using template={} to={} correlationId={}", req.emailTemplateCode(), user.getEmail(), context.getCorrelationId());
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

        return TokenResponse.builder()
                .userId(user.getId())
                .appId(appId)
                .profileId(profile.getId())
                .orgId(profile.getOrganizationId())
                .accessToken(jwtService.generateAccessToken(user, context, null, null))
                .refreshToken(jwtService.generateRefreshToken(user, context))
                .additionalInfo(additional)
                .build();
    }

    @Transactional
    public TokenResponse login(LoginRequest req, CoreContext context) {
        if ((req.getEmail() == null || req.getEmail().isBlank()) && (req.getPhone() == null || req.getPhone().isBlank()))
            throw new BadRequestException("Email or phone is required");

        UUID appId = context.getAppId();
        if (appId == null) throw new CoreException("VALIDATION_ERROR", "Header AppId App context is required");

        App app = appRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("App", "id", appId.toString()));

        // remaining login logic is currently commented
        return  null;
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest req, CoreContext context) {
        // TODO: Implement refresh token logic
        throw new BadRequestException("Not yet implemented");
    }

    @Transactional
    public void logout(String token, CoreContext context) {
        // TODO: Blacklist token in Redis
    }

    @Transactional
    public Object enrollToApp(EnrollRequest req, CoreContext context) {
        // Enrollment should ensure header appId present
        UUID appId = context.getAppId();
        if (appId == null) throw new CoreException("VALIDATION_ERROR", "Header AppId App context is required");

        // existing enrollment logic commented out — return placeholder
        return null;
    }

    public Object createOrganization(Object req) {
        // TODO: implement
        throw new BadRequestException("Not yet implemented");
    }

    public Object inviteToOrg(UUID orgId, Object req) {
        // TODO: implement
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
}
