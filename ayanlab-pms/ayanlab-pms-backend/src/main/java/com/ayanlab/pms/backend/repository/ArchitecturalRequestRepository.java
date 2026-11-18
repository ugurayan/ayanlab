package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.associations.ArchitecturalRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchitecturalRequestRepository extends IpmsOrgRepository<ArchitecturalRequest, java.util.UUID> {
}
