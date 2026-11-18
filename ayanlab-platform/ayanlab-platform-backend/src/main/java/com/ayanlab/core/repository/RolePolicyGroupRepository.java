package com.ayanlab.core.repository;

import com.ayanlab.core.entity.RolePolicyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RolePolicyGroupRepository extends JpaRepository<RolePolicyGroup, UUID> {

    List<RolePolicyGroup> findByRoleIdAndIsActiveTrue(UUID roleId);

    List<RolePolicyGroup> findByRoleIdInAndIsActiveTrue(Collection<UUID> roleIds);

    List<RolePolicyGroup> findByRoleId(UUID roleId);

    boolean existsByRoleIdAndPolicyGroupId(UUID roleId, UUID policyGroupId);
}
