package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface OrganizationService {
    List<Organization> findAll(CoreContext context);
    Organization findById(String id, CoreContext context);
    Organization create(Organization body, CoreContext context);
    Organization update(Organization body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
