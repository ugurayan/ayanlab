package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.propertymgmt.entity.rentals.PropertyType;

import java.util.List;
import java.util.UUID;

public interface IPropertyTypeService {

    PropertyType create(PropertyType item, CoreContext context);

    PropertyType update(PropertyType item, CoreContext context);

    void deleteById(UUID id, CoreContext context);

    PropertyType findById(UUID id, CoreContext context);

    List<PropertyType> findVisible(CoreContext context);
}
