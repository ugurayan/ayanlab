package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tasks.IncomingRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomingRequestRepository extends IpmsOrgRepository<IncomingRequest, java.util.UUID> {
}
