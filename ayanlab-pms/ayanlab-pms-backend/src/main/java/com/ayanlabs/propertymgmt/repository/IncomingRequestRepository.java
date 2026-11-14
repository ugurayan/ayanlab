package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.tasks.IncomingRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomingRequestRepository extends IpmsOrgRepository<IncomingRequest, java.util.UUID> {
}
