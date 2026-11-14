package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.ResidentCenterUser;
import com.ayanlabs.propertymgmt.repository.ResidentCenterUserRepository;
import com.ayanlabs.propertymgmt.service.IResidentCenterUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResidentCenterUserService extends AbstractIpmsOrgService<ResidentCenterUser, ResidentCenterUserRepository> implements IResidentCenterUserService {

    public ResidentCenterUserService(ResidentCenterUserRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "ResidentCenterUser";
    }
}
