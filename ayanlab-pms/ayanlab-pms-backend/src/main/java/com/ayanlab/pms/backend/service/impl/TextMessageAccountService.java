package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.TextMessageAccount;
import com.ayanlab.pms.backend.repository.TextMessageAccountRepository;
import com.ayanlab.pms.backend.service.ITextMessageAccountService;
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
