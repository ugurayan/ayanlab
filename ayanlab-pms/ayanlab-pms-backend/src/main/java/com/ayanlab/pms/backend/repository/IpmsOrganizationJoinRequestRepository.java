package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.signup.IpmsOrganizationJoinRequest;
import com.ayanlab.pms.backend.enums.OrganizationJoinRequestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IpmsOrganizationJoinRequestRepository extends JpaRepository<IpmsOrganizationJoinRequest, UUID> {

    Optional<IpmsOrganizationJoinRequest> findFirstByUserIdAndOrganizationIdAndStatusAndIsActiveTrue(
            UUID userId, UUID organizationId, OrganizationJoinRequestStatusEnum status);

    List<IpmsOrganizationJoinRequest> findByOrganizationIdAndStatusAndIsActiveTrueOrderByCreatedAtDesc(
            UUID organizationId, OrganizationJoinRequestStatusEnum status);
}
