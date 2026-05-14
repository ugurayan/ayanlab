package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserIdAndAppId(UUID userId, UUID appId);
//    List<UserProfile> findByUserId(UUID userId);
//    boolean existsByUserIdAndAppId(UUID userId, UUID appId);
//    Optional<UserProfile> findByAppIdAndEmail(UUID appId, String email);
//    Optional<UserProfile> findByAppIdAndPhone(UUID appId, String phone);
}

