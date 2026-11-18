package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.settings.SubscriptionAccount;
import com.ayanlab.pms.backend.repository.SubscriptionAccountRepository;
import com.ayanlab.pms.backend.service.ISubscriptionAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionAccountService extends AbstractIpmsOrgService<SubscriptionAccount, SubscriptionAccountRepository> implements ISubscriptionAccountService {

    public SubscriptionAccountService(SubscriptionAccountRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "SubscriptionAccount";
    }
}
