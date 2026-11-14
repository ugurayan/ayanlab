package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.Applicant;
import com.ayanlabs.propertymgmt.repository.ApplicantRepository;
import com.ayanlabs.propertymgmt.service.IApplicantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApplicantService extends AbstractIpmsOrgService<Applicant, ApplicantRepository> implements IApplicantService {

    public ApplicantService(ApplicantRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Applicant> findAll(CoreContext context, UUID listingId) {
        UUID orgId = requireOrgId(context);
        if (listingId != null) {
            return repository.findByOrganizationIdAndListingIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, listingId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "Applicant";
    }
}
