package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.rentals.OutstandingBalance;
import com.ayanlab.pms.backend.repository.OutstandingBalanceRepository;
import com.ayanlab.pms.backend.service.IOutstandingBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OutstandingBalanceService extends AbstractIpmsOrgService<OutstandingBalance, OutstandingBalanceRepository> implements IOutstandingBalanceService {

    public OutstandingBalanceService(OutstandingBalanceRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutstandingBalance> findAll(CoreContext context, UUID leaseId) {
        UUID orgId = requireOrgId(context);
        if (leaseId != null) {
            return repository.findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, leaseId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "OutstandingBalance";
    }
}
