package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Application;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ApplicationService {
    List<Application> findAll(CoreContext context);
    Application findById(String id, CoreContext context);
    Application create(Application body, CoreContext context);
    Application update(Application body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
