package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface WorkerCertificateService {
    List<WorkerCertificate> findAll(CoreContext context);
    WorkerCertificate findById(String id, CoreContext context);
    WorkerCertificate create(WorkerCertificate body, CoreContext context);
    WorkerCertificate update(WorkerCertificate body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
