package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.associations.Violation;
import com.ayanlabs.propertymgmt.repository.ViolationRepository;
import com.ayanlabs.propertymgmt.service.IViolationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ViolationService extends AbstractIpmsOrgService<Violation, ViolationRepository> implements IViolationService {

    public ViolationService(ViolationRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Violation> findAll(CoreContext context, UUID associationId) {
        UUID orgId = requireOrgId(context);
        if (associationId != null) {
            return repository.findByOrganizationIdAndAssociationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, associationId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "Violation";
    }
}
