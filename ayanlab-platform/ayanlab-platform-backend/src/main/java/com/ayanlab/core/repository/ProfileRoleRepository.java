package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ProfileRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileRoleRepository extends JpaRepository<ProfileRole, UUID> {

    List<ProfileRole> findByProfileIdAndIsActiveTrue(UUID profileId);

    List<ProfileRole> findByRoleId(UUID roleId);

    boolean existsByProfileIdAndRoleId(UUID profileId, UUID roleId);
}

