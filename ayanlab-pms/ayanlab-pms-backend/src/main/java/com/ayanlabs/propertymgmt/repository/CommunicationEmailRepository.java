package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.CommunicationEmail;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunicationEmailRepository extends IpmsOrgRepository<CommunicationEmail, java.util.UUID> {
}
