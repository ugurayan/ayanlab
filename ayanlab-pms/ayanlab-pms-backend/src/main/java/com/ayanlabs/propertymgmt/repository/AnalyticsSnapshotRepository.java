package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.reporting.AnalyticsSnapshot;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsSnapshotRepository extends IpmsOrgRepository<AnalyticsSnapshot, java.util.UUID> {
}
