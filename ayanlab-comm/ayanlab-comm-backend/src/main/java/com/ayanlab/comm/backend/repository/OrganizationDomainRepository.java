package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.OrganizationDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationDomainRepository extends JpaRepository<OrganizationDomain, UUID> {
    Optional<OrganizationDomain> findByDomainName(String domainName);
    List<OrganizationDomain> findByOrganizationId(UUID organizationId);
    List<OrganizationDomain> findByStatus(String status);
}
