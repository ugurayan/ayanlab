package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.inspections.PropertyInspection;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyInspectionRepository extends IpmsOrgRepository<PropertyInspection, java.util.UUID> {
}
