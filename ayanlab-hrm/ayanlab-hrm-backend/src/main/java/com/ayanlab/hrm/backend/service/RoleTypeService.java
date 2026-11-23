package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.RoleType;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface RoleTypeService {
    List<RoleType> findAll(CoreContext context);
    RoleType findById(String id, CoreContext context);
    RoleType create(RoleType body, CoreContext context);
    RoleType update(RoleType body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
