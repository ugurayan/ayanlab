package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.associations.Association;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociationRepository extends IpmsOrgRepository<Association, java.util.UUID> {
}
