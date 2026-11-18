package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfilePermissionRepository extends JpaRepository<ProfilePermission, UUID> {
    boolean existsByProfileIdAndPermissionId(UUID profileId, UUID permissionId);

    List<ProfilePermission> findByProfileIdAndIsActiveTrue(UUID profileId);

    List<ProfilePermission> findByPermissionId(UUID permissionId);
}

