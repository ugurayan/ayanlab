package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.Department;

import java.util.List;
import java.util.UUID;

public interface IDepartmentService extends IBaseService<Department> {

    List<Department> findByOrganizationId(UUID organizationId, CoreContext context);
}