package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.accounting.ChartOfAccount;
import com.ayanlab.pms.backend.repository.ChartOfAccountRepository;
import com.ayanlab.pms.backend.service.IChartOfAccountService;
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
