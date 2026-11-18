package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.rentals.RentalOwner;
import com.ayanlab.pms.backend.repository.RentalOwnerRepository;
import com.ayanlab.pms.backend.service.IRentalOwnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentalOwnerService extends AbstractIpmsOrgService<RentalOwner, RentalOwnerRepository> implements IRentalOwnerService {

    public RentalOwnerService(RentalOwnerRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "RentalOwner";
    }
}
