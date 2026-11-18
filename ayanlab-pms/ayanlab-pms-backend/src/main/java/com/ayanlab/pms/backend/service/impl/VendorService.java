package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.maintenance.Vendor;
import com.ayanlab.pms.backend.repository.VendorRepository;
import com.ayanlab.pms.backend.service.IVendorService;
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
