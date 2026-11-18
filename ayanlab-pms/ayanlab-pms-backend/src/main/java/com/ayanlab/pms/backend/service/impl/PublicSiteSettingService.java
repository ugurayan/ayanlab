package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.PublicSiteSetting;
import com.ayanlab.pms.backend.repository.PublicSiteSettingRepository;
import com.ayanlab.pms.backend.service.IPublicSiteSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PublicSiteSettingService extends AbstractIpmsOrgService<PublicSiteSetting, PublicSiteSettingRepository> implements IPublicSiteSettingService {

    public PublicSiteSettingService(PublicSiteSettingRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "PublicSiteSetting";
    }
}
