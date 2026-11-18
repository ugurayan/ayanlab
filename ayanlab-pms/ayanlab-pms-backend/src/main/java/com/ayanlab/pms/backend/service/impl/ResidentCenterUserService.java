package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.ResidentCenterUser;
import com.ayanlab.pms.backend.repository.ResidentCenterUserRepository;
import com.ayanlab.pms.backend.service.IResidentCenterUserService;
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
