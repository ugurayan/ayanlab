package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.associations.Association;
import com.ayanlabs.propertymgmt.repository.AssociationRepository;
import com.ayanlabs.propertymgmt.service.IAssociationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssociationService extends AbstractIpmsOrgService<Association, AssociationRepository> implements IAssociationService {

    public AssociationService(AssociationRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Association";
    }
}
