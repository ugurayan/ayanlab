package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Department;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<Department> findAll(CoreContext context);
    Department findById(UUID id, CoreContext context);
    Department create(Department body, CoreContext context);
    Department update(Department body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
