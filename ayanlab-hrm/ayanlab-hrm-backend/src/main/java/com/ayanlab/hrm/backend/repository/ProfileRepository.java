package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Profile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<Profile> findByProfileId(String profileId);
    List<Profile> findByOrgId(String orgId);
    List<Profile> findByWorkerId(String workerId);
}
