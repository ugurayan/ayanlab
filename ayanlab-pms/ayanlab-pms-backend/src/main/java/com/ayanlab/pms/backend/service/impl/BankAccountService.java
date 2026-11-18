package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.accounting.BankAccount;
import com.ayanlab.pms.backend.repository.BankAccountRepository;
import com.ayanlab.pms.backend.service.IBankAccountService;
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
