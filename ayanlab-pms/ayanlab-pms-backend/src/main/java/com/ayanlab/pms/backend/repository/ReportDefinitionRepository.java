package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.reporting.ReportDefinition;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDefinitionRepository extends IpmsOrgRepository<ReportDefinition, java.util.UUID> {
}
