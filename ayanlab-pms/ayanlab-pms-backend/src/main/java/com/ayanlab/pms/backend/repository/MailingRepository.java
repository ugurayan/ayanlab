package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.Mailing;
import org.springframework.stereotype.Repository;

@Repository
public interface MailingRepository extends IpmsOrgRepository<Mailing, java.util.UUID> {
}
