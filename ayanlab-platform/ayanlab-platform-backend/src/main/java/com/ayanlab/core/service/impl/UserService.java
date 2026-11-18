package com.ayanlab.core.service.impl;

import com.ayanlab.core.config.VerificationProperties;
import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.entity.User;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.data.responses.PagedResponse;
import com.ayanlab.core.repository.UserRepository;
import com.ayanlab.core.service.INotificationTemplateService;
import com.ayanlab.core.service.IPasswordHistoryService;
import com.ayanlab.core.service.IUserService;
import com.ayanlab.core.service.sms.ISmsService;
import com.ayanlab.core.service.support.IEmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final INotificationTemplateService notificationTemplateService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationProperties verificationProperties;
    private final ISmsService smsService;
    private final IEmailSender emailSender;
    private final IPasswordHistoryService passwordHistoryService;

    @Override
    @Transactional
    public User create(User item, CoreContext context) {
        item.setAppId(context.getAppId());
        item = map(item, context.getUserId(), ActionEnum.CREATE);

        // conflict: same email or mobile for the same app
        Optional<User> conflictUser = userRepository.findByEmailOrMobileAndAppId(item.getEmail(), item.getMobile(), item.getAppId());
        if (conflictUser.isPresent()) {
            if (conflictUser.get().getEmail() != null && conflictUser.get().getEmail().equalsIgnoreCase(item.getEmail())) {
                throw new CoreException("USER_CONFLICT_EMAIL", "A user with the same email already exists for this app");
            }
            if (conflictUser.get().getMobile() != null && conflictUser.get().getMobile().equals(item.getMobile())) {
                throw new CoreException("USER_CONFLICT_MOBILE", "A user with the same mobile number already exists for this app");
            }
            throw new CoreException("USER_CONFLICT", "A user with the same email or mobile already exists for this app");
        }
        return userRepository.save(item);
    }

    @Override
    @Transactional
    public User update(User item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "User id is required for update");
        }

        User currentUser = userRepository.findById(item.getId()).orElse(null);
        if (currentUser == null) {
            throw new CoreException("NOT_FOUND", "User not found");
        }
        if (currentUser.getAppId() != null && !currentUser.getAppId().equals(context.getAppId())) {
            throw new CoreException("FORBIDDEN", "Cannot modify user of a different app");
        }

        // prevent changing email or mobile to one that already exists in the same app
        if  ( (item.getEmail() != null && !item.getEmail().equalsIgnoreCase(currentUser.getEmail()))
            || (item.getMobile() != null && !item.getMobile().equals(currentUser.getMobile())) ) {
            Optional<User> otherUser= userRepository.findByEmailOrMobileAndAppIdNotId(item.getEmail(), item.getMobile(), context.getAppId(), item.getId());
            if (otherUser.isPresent() ){
                if ( otherUser.get().getEmail() != null && otherUser.get().getEmail().equalsIgnoreCase(item.getEmail())) {
                    throw new CoreException("USER_CONFLICT_EMAIL", "Another user with this email already exists for this app");
                }
                if ( otherUser.get().getMobile() != null && otherUser.get().getMobile().equals(item.getMobile())) {
                    throw new CoreException("USER_CONFLICT_MOBILE", "Another user with this mobile number already exists for this app");
                }
                throw new CoreException("USER_CONFLICT", "Another user with this email or mobile already exists for this app");
            }
        }
        item.setAppId(context.getAppId());
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return userRepository.save(item);
    }

    /**
     * Soft delete the user by setting isActive to false.
     */
    @Override
    @Transactional
    public void delete(User item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        userRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        User user = findById(id, context);
        if (user != null) {
            delete(user, context);
        }
    }

    @Override
    public User findById(UUID id, CoreContext context) {
        User user;
        try {
            user = userRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
        if (user == null) return null;
        // ensure scoping to current app
        if (user.getAppId() != null && !user.getAppId().equals(context.getAppId())) {
            throw new CoreException("FORBIDDEN", "User does not belong to the current app");
        }
        return user;
    }

    // Helpful lookups
    @Override
    public Optional<User> findByEmailAndAppId(String email, UUID appId) {
        try {
            return userRepository.findByEmailAndAppId(email, appId);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public Optional<User> findByMobileAndAppId(String mobile, UUID appId) {
        try {
            return userRepository.findByMobileAndAppId(mobile, appId);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public List<User> findAllByAppId(UUID appId, Pageable pageable) {
        try {
            return userRepository.findAllByAppId(appId, pageable);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public PagedResponse<User> findAll(int page, int size, UUID appId, CoreContext context) {
        UUID resolvedAppId = appId != null ? appId : context.getAppId();
        if (resolvedAppId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<User> p = userRepository.findPageByAppId(resolvedAppId, pageable);
        return PagedResponse.<User>builder()
            .content(p.getContent())
            .page(p.getNumber())
            .size(p.getSize())
            .totalElements(p.getTotalElements())
            .totalPages(p.getTotalPages())
            .last(p.isLast())
            .build();
    }

    // ----- verification and password flows -----
    @Override
    @Transactional
    public User generateEmailVerificationCode(UUID userId, CoreContext context) {
        User u = findById(userId, context);
        if (u == null) throw new CoreException("NOT_FOUND", "User not found");
        String code = UUID.randomUUID().toString().substring(0,6).toUpperCase();
        u.setEmailConfirmationCode(code);
        u.setEmailCodeExpiration(Instant.now().plus(
                verificationProperties.getEmailCodeExpirationMinutes(), ChronoUnit.MINUTES));
        u.setEmailCodeStatus(null);
        u = map(u, context.getUserId(), ActionEnum.UPDATE);
        userRepository.save(u);
        log.info("[generateEmailCode] email={} code={}", u.getEmail(), code);
        if (StringUtils.hasText(u.getEmail())) {
            try {
                emailSender.send(
                        u.getEmail(),
                        "Your verification code",
                        "Your email verification code is: " + code + "\n\nThis code expires in "
                                + verificationProperties.getEmailCodeExpirationMinutes() + " minutes."
                );
                log.info("[generateEmailCode] email dispatched to {}", u.getEmail());
            } catch (Exception e) {
                log.error("[generateEmailCode] email send failed to={}: {}", u.getEmail(), e.getMessage());
                // keep code in DB for debug UI / retry; surface soft failure
            }
        }
        return u;
    }

    @Override
    public boolean validateEmailCode(UUID userId, String code, CoreContext context) {
        User u = findById(userId, context);
        if (u == null) return false;
        if (u.getEmailConfirmationCode() == null) return false;
        if (!u.getEmailConfirmationCode().equals(code)) return false;
        if (u.getEmailCodeExpiration() != null && Instant.now().isAfter(u.getEmailCodeExpiration())) return false;
        u.setIsEmailVerified(true);
        u.setEmailVerificationDate(Instant.now());
        u.setEmailConfirmationCode(null);
        u.setEmailCodeExpiration(null);
        userRepository.save(map(u, context.getUserId(), ActionEnum.UPDATE));
        return true;
    }

    @Override
    @Transactional
    public User generateMobileVerificationCode(UUID userId, CoreContext context) {
        User u = findById(userId, context);
        if (u == null) throw new CoreException("NOT_FOUND", "User not found");
        String code = String.valueOf(new Random().nextInt(899999)+100000);
        u.setMobileVerificationCode(code);
        u.setMobileCodeExpiration(Instant.now().plus(
                verificationProperties.getMobileCodeExpirationMinutes(), ChronoUnit.MINUTES));
        u = map(u, context.getUserId(), ActionEnum.UPDATE);
        userRepository.save(u);
        log.info("[generateMobileCode] mobile={} code={}", u.getMobile(), code);
        if (StringUtils.hasText(u.getMobile())) {
            try {
                smsService.sendSms(
                        u.getMobile(),
                        "Your IPMS verification code is: " + code
                                + ". Expires in " + verificationProperties.getMobileCodeExpirationMinutes() + " minutes."
                );
            } catch (Exception e) {
                log.error("[generateMobileCode] SMS send failed to={}: {}", u.getMobile(), e.getMessage());
                // Keep code for resend/debug UI; do not roll back signup.
            }
        }
        return u;
    }

    @Override
    public boolean validateMobileCode(UUID userId, String code, CoreContext context) {
        User u = findById(userId, context);
        if (u == null) return false;
        if (u.getMobileVerificationCode() == null) return false;
        if (!u.getMobileVerificationCode().equals(code)) return false;
        if (u.getMobileCodeExpiration() != null && Instant.now().isAfter(u.getMobileCodeExpiration())) return false;
        u.setIsMobileVerified(true);
        u.setMobileVerificationDate(System.currentTimeMillis());
        u.setMobileVerificationCode(null);
        u.setMobileCodeExpiration(null);
        userRepository.save(map(u, context.getUserId(), ActionEnum.UPDATE));
        return true;
    }

    @Override
    @Transactional
    public String initiatePasswordReset(String emailOrMobile, UUID appId, CoreContext context) {
        Optional<User> uOpt = Optional.empty();
        if (emailOrMobile.contains("@")) {
            uOpt = userRepository.findByEmailAndAppId(emailOrMobile, appId);
        } else {
            uOpt = userRepository.findByMobileAndAppId(emailOrMobile, appId);
        }
        if (uOpt.isEmpty()) throw new CoreException("NOT_FOUND", "User not found");
        User u = uOpt.get();
        String token = UUID.randomUUID().toString();
        u.setPasswordResetToken(token);
        u.setPasswordResetExpiration(Instant.now().plus(1, ChronoUnit.HOURS));
        userRepository.save(map(u, context.getUserId(), ActionEnum.UPDATE));
        // send via template or log
        log.info("[initiatePasswordReset] sending password reset to {} token={}", emailOrMobile, token);
        return token;
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword, CoreContext context) {
        Optional<User> uOpt = userRepository.findAll().stream().filter(u -> token.equals(u.getPasswordResetToken())).findFirst();
        if (uOpt.isEmpty()) return false;
        User u = uOpt.get();
        if (u.getPasswordResetExpiration() == null || Instant.now().isAfter(u.getPasswordResetExpiration())) return false;
        u.setPassword(passwordEncoder.encode(newPassword));
        u.setPasswordResetToken(null);
        u.setPasswordResetExpiration(null);
        userRepository.save(map(u, context.getUserId(), ActionEnum.UPDATE));
        return true;
    }

    @Override
    public boolean sendUsernameReminder(String emailOrMobile, UUID appId, CoreContext context) {
        Optional<User> uOpt = Optional.empty();
        if (emailOrMobile.contains("@")) {
            uOpt = userRepository.findByEmailAndAppId(emailOrMobile, appId);
        } else {
            uOpt = userRepository.findByMobileAndAppId(emailOrMobile, appId);
        }
        if (uOpt.isEmpty()) throw new CoreException("NOT_FOUND", "User not found");
        User u = uOpt.get();
        log.info("[sendUsernameReminder] username for {} is {}", emailOrMobile, u.getEmail());
        return true;
    }

    @Override
    @Transactional
    public boolean changePassword(UUID userId, String oldPassword, String newPassword, CoreContext context) {
        User u = findById(userId, context);
        if (u == null) throw new CoreException("NOT_FOUND", "User not found");
        if (!passwordEncoder.matches(oldPassword, u.getPassword())) throw new CoreException("INVALID_CREDENTIALS", "Old password does not match");
        if (u.getPassword() != null) {
            passwordHistoryService.record(userId, u.getPassword(), context);
        }
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(map(u, context.getUserId(), ActionEnum.UPDATE));
        return true;
    }

    @Override
    @Transactional
    public User setActive(UUID userId, boolean active, CoreContext context) {
        User user = findById(userId, context);
        if (user == null) {
            throw new CoreException("NOT_FOUND", "User not found");
        }
        user.setIsActive(active);
        return userRepository.save(map(user, context.getUserId(), ActionEnum.UPDATE));
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        User found = findById(id, context);
        if (found != null) {
            userRepository.delete(found);
        }
    }
}
