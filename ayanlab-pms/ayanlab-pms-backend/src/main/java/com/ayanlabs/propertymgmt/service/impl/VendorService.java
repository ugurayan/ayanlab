package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.maintenance.Vendor;
import com.ayanlabs.propertymgmt.repository.VendorRepository;
import com.ayanlabs.propertymgmt.service.IVendorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VendorService extends AbstractIpmsOrgService<Vendor, VendorRepository> implements IVendorService {

    public VendorService(VendorRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Vendor";
    }
}
