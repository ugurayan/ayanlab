package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Application;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    List<Application> findAll(CoreContext context);
    Application findById(UUID id, CoreContext context);
    Application create(Application body, CoreContext context);
    Application update(Application body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
