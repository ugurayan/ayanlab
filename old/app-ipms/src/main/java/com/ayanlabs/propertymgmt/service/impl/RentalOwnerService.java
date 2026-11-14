package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.rentals.RentalOwner;
import com.ayanlabs.propertymgmt.repository.RentalOwnerRepository;
import com.ayanlabs.propertymgmt.service.IRentalOwnerService;
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
