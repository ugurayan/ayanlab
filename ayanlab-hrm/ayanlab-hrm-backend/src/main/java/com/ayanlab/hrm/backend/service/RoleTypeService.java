package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.RoleType;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface RoleTypeService {
    List<RoleType> findAll(CoreContext context);
    RoleType findById(UUID id, CoreContext context);
    RoleType create(RoleType body, CoreContext context);
    RoleType update(RoleType body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
