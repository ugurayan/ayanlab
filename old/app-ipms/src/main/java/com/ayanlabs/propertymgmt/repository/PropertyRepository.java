package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.rentals.Property;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends IpmsOrgRepository<Property, java.util.UUID> {
}
