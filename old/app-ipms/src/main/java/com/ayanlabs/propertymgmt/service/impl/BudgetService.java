package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.accounting.Budget;
import com.ayanlabs.propertymgmt.repository.BudgetRepository;
import com.ayanlabs.propertymgmt.service.IBudgetService;
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
