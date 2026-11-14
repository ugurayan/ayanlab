package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.associations.Violation;

public interface IViolationService extends IIpmsOrgScopedService<Violation> {

    List<Violation> findAll(CoreContext context, UUID associationId);
}
