package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.addons.AddonCatalogItem;
import org.springframework.stereotype.Repository;

@Repository
public interface AddonCatalogItemRepository extends IpmsOrgRepository<AddonCatalogItem, java.util.UUID> {
}
