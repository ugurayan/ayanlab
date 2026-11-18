package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.rentals.Property;
import com.ayanlab.pms.backend.repository.PropertyRepository;
import com.ayanlab.pms.backend.service.IPropertyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PropertyService extends AbstractIpmsOrgService<Property, PropertyRepository> implements IPropertyService {

    public PropertyService(PropertyRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Property";
    }
}
