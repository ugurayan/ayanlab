package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.RolePolicyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePolicyGroupRepository extends JpaRepository<RolePolicyGroup, UUID> {

    List<RolePolicyGroup> findByRoleIdAndIsActiveTrue(UUID roleId);

    boolean existsByRoleIdAndPolicyGroupId(UUID roleId, UUID policyGroupId);
}
