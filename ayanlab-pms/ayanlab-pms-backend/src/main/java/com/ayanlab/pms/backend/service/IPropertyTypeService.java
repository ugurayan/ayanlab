package com.ayanlab.pms.backend.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.pms.backend.entity.rentals.PropertyType;

import java.util.List;
import java.util.UUID;

public interface IPropertyTypeService {

    PropertyType create(PropertyType item, CoreContext context);

    PropertyType update(PropertyType item, CoreContext context);

    void deleteById(UUID id, CoreContext context);

    PropertyType findById(UUID id, CoreContext context);

    List<PropertyType> findVisible(CoreContext context);
}
