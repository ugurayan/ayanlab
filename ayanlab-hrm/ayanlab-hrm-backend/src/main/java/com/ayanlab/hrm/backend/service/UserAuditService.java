package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.UserAudit;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface UserAuditService {
    List<UserAudit> findAll(CoreContext context);
    UserAudit findById(UUID id, CoreContext context);
    UserAudit create(UserAudit body, CoreContext context);
    UserAudit update(UserAudit body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
