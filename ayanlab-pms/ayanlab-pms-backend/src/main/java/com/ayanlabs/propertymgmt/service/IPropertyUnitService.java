package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.rentals.PropertyUnit;

public interface IPropertyUnitService extends IIpmsOrgScopedService<PropertyUnit> {

    List<PropertyUnit> findAll(CoreContext context, UUID propertyId);
}
