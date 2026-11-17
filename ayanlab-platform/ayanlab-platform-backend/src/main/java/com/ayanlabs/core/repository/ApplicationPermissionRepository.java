package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ApplicationPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationPermissionRepository extends JpaRepository<ApplicationPermission, UUID> {

    List<ApplicationPermission> findByIsActiveTrue();

    List<ApplicationPermission> findByAppId(UUID appId);

    List<ApplicationPermission> findByAppIdAndIsActiveTrue(UUID appId);

    boolean existsByAppIdAndPermissionId(UUID appId, UUID permissionId);
}

