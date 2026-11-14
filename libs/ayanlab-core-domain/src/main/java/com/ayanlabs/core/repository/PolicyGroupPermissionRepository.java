package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.PolicyGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyGroupPermissionRepository extends JpaRepository<PolicyGroupPermission, UUID> {

    List<PolicyGroupPermission> findByPolicyGroupIdAndIsActiveTrue(UUID policyGroupId);

    boolean existsByPolicyGroupIdAndPermissionId(UUID policyGroupId, UUID permissionId);
}
