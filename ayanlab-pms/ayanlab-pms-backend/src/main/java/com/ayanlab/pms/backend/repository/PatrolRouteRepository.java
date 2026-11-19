package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.PatrolRoute;
import org.springframework.stereotype.Repository;

@Repository
public interface PatrolRouteRepository extends IpmsOrgRepository<PatrolRoute, java.util.UUID> {
}
