package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Brand;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    List<Brand> findByPlatformOrganizationId(UUID platformOrganizationId);
}
