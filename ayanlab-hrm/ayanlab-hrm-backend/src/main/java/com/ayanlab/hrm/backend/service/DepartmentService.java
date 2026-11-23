package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Department;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface DepartmentService {
    List<Department> findAll(CoreContext context);
    Department findById(String id, CoreContext context);
    Department create(Department body, CoreContext context);
    Department update(Department body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
