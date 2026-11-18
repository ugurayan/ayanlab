package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.OrgSetting;
import com.ayanlab.pms.backend.repository.OrgSettingRepository;
import com.ayanlab.pms.backend.service.IOrgSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrgSettingService extends AbstractIpmsOrgService<OrgSetting, OrgSettingRepository> implements IOrgSettingService {

    public OrgSettingService(OrgSettingRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "OrgSetting";
    }
}
