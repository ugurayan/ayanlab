package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.ResidentCenterContact;
import com.ayanlabs.propertymgmt.repository.ResidentCenterContactRepository;
import com.ayanlabs.propertymgmt.service.IResidentCenterContactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResidentCenterContactService extends AbstractIpmsOrgService<ResidentCenterContact, ResidentCenterContactRepository> implements IResidentCenterContactService {

    public ResidentCenterContactService(ResidentCenterContactRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "ResidentCenterContact";
    }
}
