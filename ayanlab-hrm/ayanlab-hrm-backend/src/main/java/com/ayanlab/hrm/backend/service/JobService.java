package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Job;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface JobService {
    List<Job> findAll(CoreContext context);
    Job findById(String id, CoreContext context);
    Job create(Job body, CoreContext context);
    Job update(Job body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
