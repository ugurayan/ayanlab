package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Profile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    List<Profile> findByProfileId(UUID profileId);
    List<Profile> findByOrgId(UUID orgId);
    List<Profile> findByWorkerId(UUID workerId);
}
