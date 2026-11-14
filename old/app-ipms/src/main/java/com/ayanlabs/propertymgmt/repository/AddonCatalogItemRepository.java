package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.addons.AddonCatalogItem;
import org.springframework.stereotype.Repository;

@Repository
public interface AddonCatalogItemRepository extends IpmsOrgRepository<AddonCatalogItem, java.util.UUID> {
}
