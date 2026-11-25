package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobDocument;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface JobDocumentService {
    List<JobDocument> findAll(CoreContext context);
    JobDocument findById(UUID id, CoreContext context);
    JobDocument create(JobDocument body, CoreContext context);
    JobDocument update(JobDocument body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
