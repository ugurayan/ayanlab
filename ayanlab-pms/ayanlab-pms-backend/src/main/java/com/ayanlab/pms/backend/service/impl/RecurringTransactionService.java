package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.accounting.RecurringTransaction;
import com.ayanlab.pms.backend.repository.RecurringTransactionRepository;
import com.ayanlab.pms.backend.service.IRecurringTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecurringTransactionService extends AbstractIpmsOrgService<RecurringTransaction, RecurringTransactionRepository> implements IRecurringTransactionService {

    public RecurringTransactionService(RecurringTransactionRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "RecurringTransaction";
    }
}
