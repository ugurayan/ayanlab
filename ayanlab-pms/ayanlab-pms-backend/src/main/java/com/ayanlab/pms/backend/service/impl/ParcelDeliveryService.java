package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.ParcelDelivery;
import com.ayanlab.pms.backend.repository.ParcelDeliveryRepository;
import com.ayanlab.pms.backend.service.IParcelDeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParcelDeliveryService extends AbstractIpmsOrgService<ParcelDelivery, ParcelDeliveryRepository> implements IParcelDeliveryService {

    public ParcelDeliveryService(ParcelDeliveryRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "ParcelDelivery";
    }
}
