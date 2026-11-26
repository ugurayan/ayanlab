package com.ayanlab.comm.backend.service;

import com.ayanlab.comm.backend.dto.DomainRegisterRequest;
import com.ayanlab.comm.backend.entity.OrganizationDomain;

import java.util.List;
import java.util.UUID;

public interface DomainVerificationService {
    OrganizationDomain registerDomain(UUID organizationId, UUID appId, DomainRegisterRequest request);
    void verifyDomain(UUID domainId);
    List<OrganizationDomain> listDomains(UUID organizationId);
    void scheduledDnsChecks();
}
