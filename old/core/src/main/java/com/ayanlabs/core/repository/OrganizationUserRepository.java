package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, UUID> {
    boolean existsByUserIdAndOrgId(UUID userId, UUID orgId);
}
