package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.maintenance.EquipmentAsset;
import com.ayanlab.pms.backend.repository.EquipmentAssetRepository;
import com.ayanlab.pms.backend.service.IEquipmentAssetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EquipmentAssetService extends AbstractIpmsOrgService<EquipmentAsset, EquipmentAssetRepository> implements IEquipmentAssetService {

    public EquipmentAssetService(EquipmentAssetRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "EquipmentAsset";
    }
}
