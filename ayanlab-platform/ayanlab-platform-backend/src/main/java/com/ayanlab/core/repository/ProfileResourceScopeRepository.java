package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ProfileResourceScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileResourceScopeRepository extends JpaRepository<ProfileResourceScope, UUID> {

    List<ProfileResourceScope> findByProfileIdAndIsActiveTrue(UUID profileId);
}
