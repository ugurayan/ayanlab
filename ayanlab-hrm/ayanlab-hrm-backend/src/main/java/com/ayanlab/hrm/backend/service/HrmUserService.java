package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.HrmUser;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface HrmUserService {
    List<HrmUser> findAll(CoreContext context);
    HrmUser findById(UUID id, CoreContext context);
    HrmUser create(HrmUser body, CoreContext context);
    HrmUser update(HrmUser body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
