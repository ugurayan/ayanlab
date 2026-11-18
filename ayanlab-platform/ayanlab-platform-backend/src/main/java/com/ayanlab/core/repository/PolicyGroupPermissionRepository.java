package com.ayanlab.core.repository;

import com.ayanlab.core.entity.PolicyGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PolicyGroupPermissionRepository extends JpaRepository<PolicyGroupPermission, UUID> {

    List<PolicyGroupPermission> findByPolicyGroupIdAndIsActiveTrue(UUID policyGroupId);

    List<PolicyGroupPermission> findByPolicyGroupIdInAndIsActiveTrue(Collection<UUID> policyGroupIds);

    List<PolicyGroupPermission> findByPermissionId(UUID permissionId);

    boolean existsByPolicyGroupIdAndPermissionId(UUID policyGroupId, UUID permissionId);
}
