package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.rentals.Property;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends IpmsOrgRepository<Property, java.util.UUID> {
}
