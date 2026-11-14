package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.Listing;

public interface IListingService extends IIpmsOrgScopedService<Listing> {

    List<Listing> findAll(CoreContext context, UUID propertyId);
}
