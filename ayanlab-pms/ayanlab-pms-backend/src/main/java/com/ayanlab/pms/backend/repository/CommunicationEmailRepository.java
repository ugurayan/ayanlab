package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.CommunicationEmail;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunicationEmailRepository extends IpmsOrgRepository<CommunicationEmail, java.util.UUID> {
}
