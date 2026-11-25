package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.leasing.LeaseRenewal;
import com.ayanlab.pms.backend.repository.LeaseRenewalRepository;
import com.ayanlab.pms.backend.service.ILeaseRenewalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeaseRenewalService extends AbstractIpmsOrgService<LeaseRenewal, LeaseRenewalRepository> implements ILeaseRenewalService {

    public LeaseRenewalService(LeaseRenewalRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaseRenewal> findAll(CoreContext context, UUID leaseId) {
        UUID orgId = requireOrgId(context);
        if (leaseId != null) {
            return repository.findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, leaseId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "LeaseRenewal";
    }
}
