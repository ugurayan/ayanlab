package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.reporting.AnalyticsSnapshot;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsSnapshotRepository extends IpmsOrgRepository<AnalyticsSnapshot, java.util.UUID> {
}
