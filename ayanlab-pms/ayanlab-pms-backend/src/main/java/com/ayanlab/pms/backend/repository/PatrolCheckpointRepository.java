package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.PatrolCheckpoint;
import org.springframework.stereotype.Repository;

@Repository
public interface PatrolCheckpointRepository extends IpmsOrgRepository<PatrolCheckpoint, java.util.UUID> {
}
