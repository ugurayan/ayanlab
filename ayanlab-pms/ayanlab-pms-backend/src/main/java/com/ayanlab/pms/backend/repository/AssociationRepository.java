package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.associations.Association;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociationRepository extends IpmsOrgRepository<Association, java.util.UUID> {
}
