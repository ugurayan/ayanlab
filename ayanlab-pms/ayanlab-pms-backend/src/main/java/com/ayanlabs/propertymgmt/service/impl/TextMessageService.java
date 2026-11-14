package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.TextMessage;
import com.ayanlabs.propertymgmt.repository.TextMessageRepository;
import com.ayanlabs.propertymgmt.service.ITextMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TextMessageService extends AbstractIpmsOrgService<TextMessage, TextMessageRepository> implements ITextMessageService {

    public TextMessageService(TextMessageRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "TextMessage";
    }
}
