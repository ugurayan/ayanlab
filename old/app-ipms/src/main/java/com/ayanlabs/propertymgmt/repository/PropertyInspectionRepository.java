package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.inspections.PropertyInspection;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyInspectionRepository extends IpmsOrgRepository<PropertyInspection, java.util.UUID> {
}
