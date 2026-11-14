package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.Mailing;
import org.springframework.stereotype.Repository;

@Repository
public interface MailingRepository extends IpmsOrgRepository<Mailing, java.util.UUID> {
}
