package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.rentals.Property;
import com.ayanlabs.propertymgmt.repository.PropertyRepository;
import com.ayanlabs.propertymgmt.service.IPropertyService;
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
