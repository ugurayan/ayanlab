package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.leasing.Listing;

public interface IListingService extends IIpmsOrgScopedService<Listing> {

    List<Listing> findAll(CoreContext context, UUID propertyId);
}
