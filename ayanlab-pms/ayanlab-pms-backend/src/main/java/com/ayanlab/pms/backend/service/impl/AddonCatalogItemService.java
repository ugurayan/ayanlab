package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.addons.AddonCatalogItem;
import com.ayanlab.pms.backend.repository.AddonCatalogItemRepository;
import com.ayanlab.pms.backend.service.IAddonCatalogItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddonCatalogItemService extends AbstractIpmsOrgService<AddonCatalogItem, AddonCatalogItemRepository> implements IAddonCatalogItemService {

    public AddonCatalogItemService(AddonCatalogItemRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "AddonCatalogItem";
    }
}
