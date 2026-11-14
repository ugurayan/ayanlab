package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.EmailTemplate;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends IpmsOrgRepository<EmailTemplate, java.util.UUID> {
}
