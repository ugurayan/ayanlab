package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.associations.OwnershipAccount;

public interface IOwnershipAccountService extends IIpmsOrgScopedService<OwnershipAccount> {

    List<OwnershipAccount> findAll(CoreContext context, UUID associationId);
}
