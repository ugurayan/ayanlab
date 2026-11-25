package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ProfileLocationDetailService {
    List<ProfileLocationDetail> findAll(CoreContext context);
    ProfileLocationDetail findById(UUID id, CoreContext context);
    ProfileLocationDetail create(ProfileLocationDetail body, CoreContext context);
    ProfileLocationDetail update(ProfileLocationDetail body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
