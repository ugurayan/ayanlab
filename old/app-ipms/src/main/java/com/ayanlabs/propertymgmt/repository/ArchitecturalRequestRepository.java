package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.associations.ArchitecturalRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchitecturalRequestRepository extends IpmsOrgRepository<ArchitecturalRequest, java.util.UUID> {
}
