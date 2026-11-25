package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Profile;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ProfileService {
    List<Profile> findAll(CoreContext context);
    Profile findById(UUID id, CoreContext context);
    Profile create(Profile body, CoreContext context);
    Profile update(Profile body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
