package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ProfileContact;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ProfileContactService {
    List<ProfileContact> findAll(CoreContext context);
    ProfileContact findById(UUID id, CoreContext context);
    ProfileContact create(ProfileContact body, CoreContext context);
    ProfileContact update(ProfileContact body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
