package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.EnforcementCase;
import com.ayanlab.pms.backend.repository.EnforcementCaseRepository;
import com.ayanlab.pms.backend.service.IEnforcementCaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnforcementCaseService extends AbstractIpmsOrgService<EnforcementCase, EnforcementCaseRepository> implements IEnforcementCaseService {

    public EnforcementCaseService(EnforcementCaseRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "EnforcementCase";
    }
}
