package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobDocument;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface JobDocumentService {
    List<JobDocument> findAll(CoreContext context);
    JobDocument findById(String id, CoreContext context);
    JobDocument create(JobDocument body, CoreContext context);
    JobDocument update(JobDocument body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
