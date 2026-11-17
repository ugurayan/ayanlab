package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findFirstByCodeAndOrganizationId(String code, UUID organizationId);

    List<Department> findByOrganizationIdAndIsActiveTrueOrderByNameAsc(UUID organizationId);
}