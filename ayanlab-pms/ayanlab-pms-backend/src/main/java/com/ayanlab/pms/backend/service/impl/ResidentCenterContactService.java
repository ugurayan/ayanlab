package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.ResidentCenterContact;
import com.ayanlab.pms.backend.repository.ResidentCenterContactRepository;
import com.ayanlab.pms.backend.service.IResidentCenterContactService;
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
