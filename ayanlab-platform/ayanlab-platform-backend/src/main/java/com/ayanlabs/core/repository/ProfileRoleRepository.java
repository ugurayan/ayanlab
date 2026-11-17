package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ProfileRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileRoleRepository extends JpaRepository<ProfileRole, UUID> {

    List<ProfileRole> findByProfileIdAndIsActiveTrue(UUID profileId);

    boolean existsByProfileIdAndRoleId(UUID profileId, UUID roleId);
}

