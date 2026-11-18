package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.maintenance.MaintenanceRequest;
import com.ayanlab.pms.backend.repository.MaintenanceRequestRepository;
import com.ayanlab.pms.backend.service.IMaintenanceRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MaintenanceRequestService extends AbstractIpmsOrgService<MaintenanceRequest, MaintenanceRequestRepository> implements IMaintenanceRequestService {

    public MaintenanceRequestService(MaintenanceRequestRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequest> findAll(CoreContext context, UUID propertyId, UUID unitId) {
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
        return "MaintenanceRequest";
    }
}
