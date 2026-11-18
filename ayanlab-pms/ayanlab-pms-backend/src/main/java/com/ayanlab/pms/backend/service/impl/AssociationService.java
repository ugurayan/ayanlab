package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.associations.Association;
import com.ayanlab.pms.backend.repository.AssociationRepository;
import com.ayanlab.pms.backend.service.IAssociationService;
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
