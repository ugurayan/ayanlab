package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.EmailTemplate;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends IpmsOrgRepository<EmailTemplate, java.util.UUID> {
}
