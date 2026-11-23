package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface FreeTimeWindowService {
    List<FreeTimeWindow> findAll(CoreContext context);
    FreeTimeWindow findById(String id, CoreContext context);
    FreeTimeWindow create(FreeTimeWindow body, CoreContext context);
    FreeTimeWindow update(FreeTimeWindow body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
