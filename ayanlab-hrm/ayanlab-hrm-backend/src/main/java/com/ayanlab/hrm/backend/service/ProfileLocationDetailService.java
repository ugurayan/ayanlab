package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ProfileLocationDetailService {
    List<ProfileLocationDetail> findAll(CoreContext context);
    ProfileLocationDetail findById(String id, CoreContext context);
    ProfileLocationDetail create(ProfileLocationDetail body, CoreContext context);
    ProfileLocationDetail update(ProfileLocationDetail body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
