package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Profile;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ProfileService {
    List<Profile> findAll(CoreContext context);
    Profile findById(String id, CoreContext context);
    Profile create(Profile body, CoreContext context);
    Profile update(Profile body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
