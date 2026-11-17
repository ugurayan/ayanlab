package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.Department;

import java.util.List;
import java.util.UUID;

public interface IDepartmentService extends IBaseService<Department> {

    List<Department> findByOrganizationId(UUID organizationId, CoreContext context);
}