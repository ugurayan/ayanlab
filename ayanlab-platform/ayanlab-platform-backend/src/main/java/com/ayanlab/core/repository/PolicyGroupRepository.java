package com.ayanlab.core.repository;

import com.ayanlab.core.entity.PolicyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyGroupRepository extends JpaRepository<PolicyGroup, UUID> {

    Optional<PolicyGroup> findByAppIdAndCode(UUID appId, String code);

    List<PolicyGroup> findByAppIdAndIsActiveTrueOrderByCodeAsc(UUID appId);
}
