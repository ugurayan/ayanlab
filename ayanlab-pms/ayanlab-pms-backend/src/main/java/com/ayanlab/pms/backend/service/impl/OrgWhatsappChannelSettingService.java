package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.OrgWhatsappChannelSetting;
import com.ayanlab.pms.backend.repository.OrgWhatsappChannelSettingRepository;
import com.ayanlab.pms.backend.service.IOrgWhatsappChannelSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrgWhatsappChannelSettingService extends AbstractIpmsOrgService<OrgWhatsappChannelSetting, OrgWhatsappChannelSettingRepository> implements IOrgWhatsappChannelSettingService {

    public OrgWhatsappChannelSettingService(OrgWhatsappChannelSettingRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "OrgWhatsappChannelSetting";
    }
}
