package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ProfileLanguage;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ProfileLanguageService {
    List<ProfileLanguage> findAll(CoreContext context);
    ProfileLanguage findById(String id, CoreContext context);
    ProfileLanguage create(ProfileLanguage body, CoreContext context);
    ProfileLanguage update(ProfileLanguage body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
