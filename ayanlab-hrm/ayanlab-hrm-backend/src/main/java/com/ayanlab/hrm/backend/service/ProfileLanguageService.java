package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ProfileLanguage;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ProfileLanguageService {
    List<ProfileLanguage> findAll(CoreContext context);
    ProfileLanguage findById(UUID id, CoreContext context);
    ProfileLanguage create(ProfileLanguage body, CoreContext context);
    ProfileLanguage update(ProfileLanguage body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
