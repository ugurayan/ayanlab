package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface WorkerCertificateService {
    List<WorkerCertificate> findAll(CoreContext context);
    WorkerCertificate findById(UUID id, CoreContext context);
    WorkerCertificate create(WorkerCertificate body, CoreContext context);
    WorkerCertificate update(WorkerCertificate body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
