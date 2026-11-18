package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.AutomatedEmailSetting;
import com.ayanlab.pms.backend.repository.AutomatedEmailSettingRepository;
import com.ayanlab.pms.backend.service.IAutomatedEmailSettingService;
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
