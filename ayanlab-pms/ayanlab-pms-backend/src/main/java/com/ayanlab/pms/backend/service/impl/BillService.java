package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.accounting.Bill;
import com.ayanlab.pms.backend.repository.BillRepository;
import com.ayanlab.pms.backend.service.IBillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BillService extends AbstractIpmsOrgService<Bill, BillRepository> implements IBillService {

    public BillService(BillRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bill> findAll(CoreContext context, UUID propertyId, UUID vendorId) {
        UUID orgId = requireOrgId(context);
        if (vendorId != null) {
            return repository.findByOrganizationIdAndVendorIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, vendorId);
        }
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "Bill";
    }
}
