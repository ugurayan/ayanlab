package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.SystemAudit;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface SystemAuditService {
    List<SystemAudit> findAll(CoreContext context);
    SystemAudit findById(String id, CoreContext context);
    SystemAudit create(SystemAudit body, CoreContext context);
    SystemAudit update(SystemAudit body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
