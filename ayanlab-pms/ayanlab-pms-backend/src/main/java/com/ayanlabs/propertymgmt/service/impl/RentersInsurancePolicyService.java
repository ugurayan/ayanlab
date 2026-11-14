package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.leasing.RentersInsurancePolicy;
import com.ayanlabs.propertymgmt.repository.RentersInsurancePolicyRepository;
import com.ayanlabs.propertymgmt.service.IRentersInsurancePolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentersInsurancePolicyService
        extends AbstractIpmsOrgService<RentersInsurancePolicy, RentersInsurancePolicyRepository>
        implements IRentersInsurancePolicyService {

    public RentersInsurancePolicyService(RentersInsurancePolicyRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "RentersInsurancePolicy";
    }
}
