package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    List<Department> findByProfileId(String profileId);
    List<Department> findByOrgId(String orgId);
    List<Department> findByWorkerId(String workerId);
}
