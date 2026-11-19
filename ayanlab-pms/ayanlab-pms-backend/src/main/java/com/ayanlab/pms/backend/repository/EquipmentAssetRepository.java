package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.maintenance.EquipmentAsset;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentAssetRepository extends IpmsOrgRepository<EquipmentAsset, java.util.UUID> {
}
