package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.settings.SubscriptionAccount;
import com.ayanlabs.propertymgmt.repository.SubscriptionAccountRepository;
import com.ayanlabs.propertymgmt.service.ISubscriptionAccountService;
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
