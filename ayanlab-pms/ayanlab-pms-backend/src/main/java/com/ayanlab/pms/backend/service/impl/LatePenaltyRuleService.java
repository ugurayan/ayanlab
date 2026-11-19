package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.LatePenaltyRule;
import com.ayanlab.pms.backend.repository.LatePenaltyRuleRepository;
import com.ayanlab.pms.backend.service.ILatePenaltyRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LatePenaltyRuleService extends AbstractIpmsOrgService<LatePenaltyRule, LatePenaltyRuleRepository> implements ILatePenaltyRuleService {

    public LatePenaltyRuleService(LatePenaltyRuleRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "LatePenaltyRule";
    }
}
