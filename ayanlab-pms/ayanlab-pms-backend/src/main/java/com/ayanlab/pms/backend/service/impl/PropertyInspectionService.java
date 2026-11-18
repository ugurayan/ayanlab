package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.inspections.PropertyInspection;
import com.ayanlab.pms.backend.repository.PropertyInspectionRepository;
import com.ayanlab.pms.backend.service.IPropertyInspectionService;
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
