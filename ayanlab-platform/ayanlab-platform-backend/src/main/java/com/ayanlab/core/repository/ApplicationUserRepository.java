package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, UUID> {

    boolean existsByAppIdAndUserId(UUID appId, UUID userId);

    List<ApplicationUser> findByIsActiveTrue();

    List<ApplicationUser> findByAppId(UUID appId);

    List<ApplicationUser> findByAppIdAndIsActiveTrue(UUID appId);
}
