package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.leasing.Applicant;

public interface IApplicantService extends IIpmsOrgScopedService<Applicant> {

    List<Applicant> findAll(CoreContext context, UUID listingId);
}
