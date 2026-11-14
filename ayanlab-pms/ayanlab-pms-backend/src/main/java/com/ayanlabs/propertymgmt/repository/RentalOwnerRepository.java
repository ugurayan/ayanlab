package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.rentals.RentalOwner;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalOwnerRepository extends IpmsOrgRepository<RentalOwner, java.util.UUID> {
}
