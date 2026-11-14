package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.Applicant;

public interface IApplicantService extends IIpmsOrgScopedService<Applicant> {

    List<Applicant> findAll(CoreContext context, UUID listingId);
}
