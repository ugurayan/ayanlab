package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.SystemAudit;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface SystemAuditService {
    List<SystemAudit> findAll(CoreContext context);
    SystemAudit findById(UUID id, CoreContext context);
    SystemAudit create(SystemAudit body, CoreContext context);
    SystemAudit update(SystemAudit body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
