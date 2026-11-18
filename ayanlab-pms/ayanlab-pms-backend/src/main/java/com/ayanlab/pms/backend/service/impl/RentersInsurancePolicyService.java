package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.leasing.RentersInsurancePolicy;
import com.ayanlab.pms.backend.repository.RentersInsurancePolicyRepository;
import com.ayanlab.pms.backend.service.IRentersInsurancePolicyService;
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
