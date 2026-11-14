package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.Lease;
import com.ayanlabs.propertymgmt.repository.LeaseRepository;
import com.ayanlabs.propertymgmt.service.ILeaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeaseService extends AbstractIpmsOrgService<Lease, LeaseRepository> implements ILeaseService {

    public LeaseService(LeaseRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> findAll(CoreContext context, UUID propertyId, UUID unitId) {
        UUID orgId = requireOrgId(context);
        if (unitId != null) {
            return repository.findByOrganizationIdAndUnitIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, unitId);
        }
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "Lease";
    }
}
