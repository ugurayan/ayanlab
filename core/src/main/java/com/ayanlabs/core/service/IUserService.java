package com.ayanlabs.core.service;

import com.ayanlabs.core.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ayanlabs.core.data.CoreContext;

public interface IUserService extends IBaseService<User> {
    Optional<User> findByEmailAndAppId(String email, UUID appId);

    List<User> findAllByAppId(UUID appId, Pageable pageable);

    Optional<User> findByMobileAndAppId(String mobile, UUID appId);

    // Verification & password management
    User generateEmailVerificationCode(UUID userId, CoreContext context);
    boolean validateEmailCode(UUID userId, String code, CoreContext context);

    User generateMobileVerificationCode(UUID userId, CoreContext context);
    boolean validateMobileCode(UUID userId, String code, CoreContext context);

    String initiatePasswordReset(String emailOrMobile, UUID appId, CoreContext context);
    boolean resetPassword(String token, String newPassword, CoreContext context);

    boolean sendUsernameReminder(String emailOrMobile, UUID appId, CoreContext context);

    boolean changePassword(UUID userId, String oldPassword, String newPassword, CoreContext context);
}
