package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.accounting.FinancialRecord;
import com.ayanlabs.propertymgmt.repository.FinancialRecordRepository;
import com.ayanlabs.propertymgmt.service.IFinancialRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FinancialRecordService extends AbstractIpmsOrgService<FinancialRecord, FinancialRecordRepository> implements IFinancialRecordService {

    public FinancialRecordService(FinancialRecordRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecord> findAll(CoreContext context, UUID propertyId) {
        UUID orgId = requireOrgId(context);
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "FinancialRecord";
    }
}
