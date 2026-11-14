package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.signup.IpmsSignupSession;
import com.ayanlabs.propertymgmt.data.enums.OrganizationJoinRequestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IpmsSignupSessionRepository extends JpaRepository<IpmsSignupSession, UUID> {

    Optional<IpmsSignupSession> findByUserIdAndAppIdAndIsActiveTrue(UUID userId, UUID appId);

    Optional<IpmsSignupSession> findByIdAndUserId(UUID id, UUID userId);

    Optional<IpmsSignupSession> findByJoinRequestIdAndIsActiveTrue(UUID joinRequestId);
}
