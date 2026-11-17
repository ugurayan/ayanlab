package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    List<RolePermission> findByRoleIdAndIsActiveTrue(UUID roleId);
}

