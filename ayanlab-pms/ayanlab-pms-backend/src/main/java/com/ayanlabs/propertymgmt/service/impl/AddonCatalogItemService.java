package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.addons.AddonCatalogItem;
import com.ayanlabs.propertymgmt.repository.AddonCatalogItemRepository;
import com.ayanlabs.propertymgmt.service.IAddonCatalogItemService;
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
