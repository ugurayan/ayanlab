package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ProfileLanguage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileLanguageRepository extends JpaRepository<ProfileLanguage, UUID> {
    List<ProfileLanguage> findByProfileId(UUID profileId);
    List<ProfileLanguage> findByOrgId(UUID orgId);
    List<ProfileLanguage> findByWorkerId(UUID workerId);
}
