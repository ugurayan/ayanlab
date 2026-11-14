package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.onboarding.IpmsOnboardingProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IpmsOnboardingProfileRepository extends JpaRepository<IpmsOnboardingProfile, UUID> {

    Optional<IpmsOnboardingProfile> findByIdAndIsActiveTrue(UUID id);

    Optional<IpmsOnboardingProfile> findByOrganizationIdAndIsActiveTrue(UUID organizationId);

    Optional<IpmsOnboardingProfile> findByWorkspaceShortCodeIgnoreCaseAndIsActiveTrue(String workspaceShortCode);

    java.util.List<IpmsOnboardingProfile> findByIsActiveTrue();
}
