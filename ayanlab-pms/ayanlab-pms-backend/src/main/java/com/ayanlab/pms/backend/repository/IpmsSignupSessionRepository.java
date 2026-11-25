package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.signup.IpmsSignupSession;
import com.ayanlab.pms.backend.enums.OrganizationJoinRequestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IpmsSignupSessionRepository extends JpaRepository<IpmsSignupSession, UUID> {

    Optional<IpmsSignupSession> findByUserIdAndAppIdAndIsActiveTrue(UUID userId, UUID appId);

    Optional<IpmsSignupSession> findByIdAndUserId(UUID id, UUID userId);

    Optional<IpmsSignupSession> findByJoinRequestIdAndIsActiveTrue(UUID joinRequestId);
}
