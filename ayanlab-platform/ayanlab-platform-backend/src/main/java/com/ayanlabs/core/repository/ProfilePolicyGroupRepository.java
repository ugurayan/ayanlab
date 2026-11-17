package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ProfilePolicyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfilePolicyGroupRepository extends JpaRepository<ProfilePolicyGroup, UUID> {

    List<ProfilePolicyGroup> findByProfileIdAndIsActiveTrue(UUID profileId);

    boolean existsByProfileIdAndPolicyGroupId(UUID profileId, UUID policyGroupId);
}
