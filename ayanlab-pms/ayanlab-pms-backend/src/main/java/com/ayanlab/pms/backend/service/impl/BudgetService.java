package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.accounting.Budget;
import com.ayanlab.pms.backend.repository.BudgetRepository;
import com.ayanlab.pms.backend.service.IBudgetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BudgetService extends AbstractIpmsOrgService<Budget, BudgetRepository> implements IBudgetService {

    public BudgetService(BudgetRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Budget";
    }
}
