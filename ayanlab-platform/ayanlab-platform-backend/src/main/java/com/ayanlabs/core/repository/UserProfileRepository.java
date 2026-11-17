package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserIdAndAppId(UUID userId, UUID appId);

    List<UserProfile> findByAppIdAndIsActiveTrueOrderByCreatedAtDesc(UUID appId);

    List<UserProfile> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UUID userId);

    List<UserProfile> findByOrganizationIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(UUID organizationId, UUID appId);

    Optional<UserProfile> findByUserIdAndAppIdAndProfileSlugAndIsActiveTrue(UUID userId, UUID appId, String profileSlug);

    List<UserProfile> findByUserIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(UUID userId, UUID appId);
}

