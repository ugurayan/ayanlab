package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.PublicSiteSetting;
import com.ayanlabs.propertymgmt.repository.PublicSiteSettingRepository;
import com.ayanlabs.propertymgmt.service.IPublicSiteSettingService;
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
