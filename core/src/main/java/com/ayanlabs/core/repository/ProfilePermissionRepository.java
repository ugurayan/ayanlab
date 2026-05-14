package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfilePermissionRepository extends JpaRepository<ProfilePermission, UUID> {
    boolean existsByProfileIdAndPermissionId(UUID profileId, UUID permissionId);
}

