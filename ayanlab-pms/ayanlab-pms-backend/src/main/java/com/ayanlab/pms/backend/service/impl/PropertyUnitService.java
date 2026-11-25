package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.rentals.PropertyUnit;
import com.ayanlab.pms.backend.repository.PropertyUnitRepository;
import com.ayanlab.pms.backend.service.IPropertyUnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PropertyUnitService extends AbstractIpmsOrgService<PropertyUnit, PropertyUnitRepository> implements IPropertyUnitService {

    public PropertyUnitService(PropertyUnitRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyUnit> findAll(CoreContext context, UUID propertyId) {
        UUID orgId = requireOrgId(context);
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "PropertyUnit";
    }
}
