package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ProfileContact;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ProfileContactService {
    List<ProfileContact> findAll(CoreContext context);
    ProfileContact findById(String id, CoreContext context);
    ProfileContact create(ProfileContact body, CoreContext context);
    ProfileContact update(ProfileContact body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
