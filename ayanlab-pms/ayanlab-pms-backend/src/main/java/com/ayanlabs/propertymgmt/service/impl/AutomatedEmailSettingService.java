package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.AutomatedEmailSetting;
import com.ayanlabs.propertymgmt.repository.AutomatedEmailSettingRepository;
import com.ayanlabs.propertymgmt.service.IAutomatedEmailSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AutomatedEmailSettingService extends AbstractIpmsOrgService<AutomatedEmailSetting, AutomatedEmailSettingRepository> implements IAutomatedEmailSettingService {

    public AutomatedEmailSettingService(AutomatedEmailSettingRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "AutomatedEmailSetting";
    }
}
