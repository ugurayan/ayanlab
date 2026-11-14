package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.rentals.OutstandingBalance;
import com.ayanlabs.propertymgmt.repository.OutstandingBalanceRepository;
import com.ayanlabs.propertymgmt.service.IOutstandingBalanceService;
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
