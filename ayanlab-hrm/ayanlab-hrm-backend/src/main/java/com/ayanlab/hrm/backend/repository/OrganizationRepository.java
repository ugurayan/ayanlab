package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, String> {}