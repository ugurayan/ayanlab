package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.signup.IpmsOrganizationJoinRequest;
import com.ayanlabs.propertymgmt.data.enums.OrganizationJoinRequestStatusEnum;
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
