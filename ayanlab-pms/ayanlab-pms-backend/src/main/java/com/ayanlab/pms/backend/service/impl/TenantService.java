package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.pms.backend.entity.leasing.Tenant;
import com.ayanlab.pms.backend.repository.TenantRepository;
import com.ayanlab.pms.backend.service.ITenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TenantService extends AbstractIpmsOrgService<Tenant, TenantRepository> implements ITenantService {

    public TenantService(TenantRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Tenant";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> findAll(CoreContext context, UUID propertyId, UUID unitId, UUID leaseId) {
        UUID orgId = requireOrgId(context);
        if (leaseId != null) {
            return repository.findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, leaseId);
        }
        if (unitId != null) {
            return repository.findByOrganizationIdAndUnitIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, unitId);
        }
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }
}
