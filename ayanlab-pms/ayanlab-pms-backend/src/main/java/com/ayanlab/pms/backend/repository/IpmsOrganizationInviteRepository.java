package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.signup.IpmsOrganizationInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IpmsOrganizationInviteRepository extends JpaRepository<IpmsOrganizationInvite, UUID> {

    Optional<IpmsOrganizationInvite> findByInviteCodeAndIsActiveTrue(String inviteCode);

    List<IpmsOrganizationInvite> findByOrganizationIdAndIsActiveTrueOrderByCreatedAtDesc(UUID organizationId);
}
