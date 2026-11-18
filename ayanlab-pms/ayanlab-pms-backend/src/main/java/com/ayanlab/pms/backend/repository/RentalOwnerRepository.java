package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.rentals.RentalOwner;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalOwnerRepository extends IpmsOrgRepository<RentalOwner, java.util.UUID> {
}
