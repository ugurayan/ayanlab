package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ProfileUserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileUserTypeRepository extends JpaRepository<ProfileUserType, UUID> {
    boolean existsByProfileIdAndUserTypeId(UUID profileId, UUID userTypeId);
}
