package com.ayanlab.core.repository;

import com.ayanlab.core.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findFirstByCode(String code);
    Page<Organization> findByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(String code, String name, Pageable pageable);
    Page<Organization> findByCodeContainingIgnoreCase(String code, Pageable pageable);
    Page<Organization> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
