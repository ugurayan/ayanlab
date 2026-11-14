package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.associations.OwnershipAccount;
import com.ayanlabs.propertymgmt.repository.OwnershipAccountRepository;
import com.ayanlabs.propertymgmt.service.IOwnershipAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnershipAccountService extends AbstractIpmsOrgService<OwnershipAccount, OwnershipAccountRepository> implements IOwnershipAccountService {

    public OwnershipAccountService(OwnershipAccountRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwnershipAccount> findAll(CoreContext context, UUID associationId) {
        UUID orgId = requireOrgId(context);
        if (associationId != null) {
            return repository.findByOrganizationIdAndAssociationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, associationId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "OwnershipAccount";
    }
}
