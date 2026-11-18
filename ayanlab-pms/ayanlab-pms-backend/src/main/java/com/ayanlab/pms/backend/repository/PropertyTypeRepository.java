package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.rentals.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyTypeRepository extends JpaRepository<PropertyType, UUID> {

    Optional<PropertyType> findByOrganizationIdIsNullAndCode(String code);

    Optional<PropertyType> findByOrganizationIdAndCode(UUID organizationId, String code);

    @Query("""
        SELECT pt FROM PropertyType pt
        WHERE pt.isActive = true
          AND (pt.organizationId IS NULL OR pt.organizationId = :organizationId)
        ORDER BY pt.sortOrder ASC, pt.name ASC
        """)
    List<PropertyType> findVisibleForOrganization(@Param("organizationId") UUID organizationId);

    @Query("""
        SELECT pt FROM PropertyType pt
        WHERE pt.isActive = true AND pt.organizationId IS NULL
        ORDER BY pt.sortOrder ASC, pt.name ASC
        """)
    List<PropertyType> findSystemDefaults();
}
