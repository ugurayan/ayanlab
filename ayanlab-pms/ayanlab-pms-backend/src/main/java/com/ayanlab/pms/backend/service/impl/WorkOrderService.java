package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.maintenance.WorkOrder;
import com.ayanlab.pms.backend.repository.WorkOrderRepository;
import com.ayanlab.pms.backend.service.IWorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkOrderService extends AbstractIpmsOrgService<WorkOrder, WorkOrderRepository> implements IWorkOrderService {

    public WorkOrderService(WorkOrderRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> findAll(CoreContext context, UUID maintenanceRequestId) {
        UUID orgId = requireOrgId(context);
        if (maintenanceRequestId != null) {
            return repository.findByOrganizationIdAndMaintenanceRequestIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, maintenanceRequestId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "WorkOrder";
    }
}
