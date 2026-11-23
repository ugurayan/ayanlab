package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ProfileLanguage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLanguageRepository extends JpaRepository<ProfileLanguage, String> {
    List<ProfileLanguage> findByProfileId(String profileId);
    List<ProfileLanguage> findByOrgId(String orgId);
    List<ProfileLanguage> findByWorkerId(String workerId);
}
