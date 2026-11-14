package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.OrgSetting;
import com.ayanlabs.propertymgmt.repository.OrgSettingRepository;
import com.ayanlabs.propertymgmt.service.IOrgSettingService;
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
