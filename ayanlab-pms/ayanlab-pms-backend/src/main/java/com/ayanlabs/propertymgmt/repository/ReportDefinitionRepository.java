package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.reporting.ReportDefinition;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDefinitionRepository extends IpmsOrgRepository<ReportDefinition, java.util.UUID> {
}
