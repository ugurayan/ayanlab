package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.accounting.BankAccount;
import com.ayanlabs.propertymgmt.repository.BankAccountRepository;
import com.ayanlabs.propertymgmt.service.IBankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankAccountService extends AbstractIpmsOrgService<BankAccount, BankAccountRepository> implements IBankAccountService {

    public BankAccountService(BankAccountRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "BankAccount";
    }
}
