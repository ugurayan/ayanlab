package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.inspections.PropertyInspection;
import com.ayanlabs.propertymgmt.repository.PropertyInspectionRepository;
import com.ayanlabs.propertymgmt.service.IPropertyInspectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PropertyInspectionService extends AbstractIpmsOrgService<PropertyInspection, PropertyInspectionRepository> implements IPropertyInspectionService {

    public PropertyInspectionService(PropertyInspectionRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "PropertyInspection";
    }
}
