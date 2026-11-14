package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.accounting.RecurringTransaction;
import com.ayanlabs.propertymgmt.repository.RecurringTransactionRepository;
import com.ayanlabs.propertymgmt.service.IRecurringTransactionService;
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
