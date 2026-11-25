package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface FreeTimeWindowService {
    List<FreeTimeWindow> findAll(CoreContext context);
    FreeTimeWindow findById(UUID id, CoreContext context);
    FreeTimeWindow create(FreeTimeWindow body, CoreContext context);
    FreeTimeWindow update(FreeTimeWindow body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
