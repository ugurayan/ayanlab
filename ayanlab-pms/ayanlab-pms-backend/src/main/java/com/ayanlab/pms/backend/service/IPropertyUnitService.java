package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.rentals.PropertyUnit;

public interface IPropertyUnitService extends IIpmsOrgScopedService<PropertyUnit> {

    List<PropertyUnit> findAll(CoreContext context, UUID propertyId);
}
