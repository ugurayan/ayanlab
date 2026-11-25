package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<Organization> findAll(CoreContext context);
    Organization findById(UUID id, CoreContext context);
    Organization create(Organization body, CoreContext context);
    Organization update(Organization body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
