package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.HrmUser;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface HrmUserService {
    List<HrmUser> findAll(CoreContext context);
    HrmUser findById(String id, CoreContext context);
    HrmUser create(HrmUser body, CoreContext context);
    HrmUser update(HrmUser body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
