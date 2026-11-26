package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.SuppressionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuppressionListRepository extends JpaRepository<SuppressionList, UUID> {
    Optional<SuppressionList> findByOrganizationIdAndEmail(UUID organizationId, String email);
    boolean existsByOrganizationIdAndEmail(UUID organizationId, String email);
}
