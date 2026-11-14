package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.associations.OwnershipAccount;

public interface IOwnershipAccountService extends IIpmsOrgScopedService<OwnershipAccount> {

    List<OwnershipAccount> findAll(CoreContext context, UUID associationId);
}
