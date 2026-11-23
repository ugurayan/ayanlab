package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.UserAudit;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface UserAuditService {
    List<UserAudit> findAll(CoreContext context);
    UserAudit findById(String id, CoreContext context);
    UserAudit create(UserAudit body, CoreContext context);
    UserAudit update(UserAudit body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
