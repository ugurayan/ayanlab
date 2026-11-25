package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByProfileId(UUID profileId);
    List<Department> findByOrgId(UUID orgId);
    List<Department> findByWorkerId(UUID workerId);
}
