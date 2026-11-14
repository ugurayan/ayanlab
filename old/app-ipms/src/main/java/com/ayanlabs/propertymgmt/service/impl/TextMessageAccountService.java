package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.TextMessageAccount;
import com.ayanlabs.propertymgmt.repository.TextMessageAccountRepository;
import com.ayanlabs.propertymgmt.service.ITextMessageAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TextMessageAccountService extends AbstractIpmsOrgService<TextMessageAccount, TextMessageAccountRepository> implements ITextMessageAccountService {

    public TextMessageAccountService(TextMessageAccountRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "TextMessageAccount";
    }
}
