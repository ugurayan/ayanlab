package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.accounting.ChartOfAccount;
import com.ayanlabs.propertymgmt.repository.ChartOfAccountRepository;
import com.ayanlabs.propertymgmt.service.IChartOfAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChartOfAccountService extends AbstractIpmsOrgService<ChartOfAccount, ChartOfAccountRepository> implements IChartOfAccountService {

    public ChartOfAccountService(ChartOfAccountRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "ChartOfAccount";
    }
}
