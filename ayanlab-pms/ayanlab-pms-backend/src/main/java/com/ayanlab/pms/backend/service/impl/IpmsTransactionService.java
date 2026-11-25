package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.accounting.IpmsTransaction;
import com.ayanlab.pms.backend.repository.IpmsTransactionRepository;
import com.ayanlab.pms.backend.service.IIpmsTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IpmsTransactionService extends AbstractIpmsOrgService<IpmsTransaction, IpmsTransactionRepository> implements IIpmsTransactionService {

    public IpmsTransactionService(IpmsTransactionRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpmsTransaction> findAll(CoreContext context, UUID propertyId) {
        UUID orgId = requireOrgId(context);
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "IpmsTransaction";
    }
}
