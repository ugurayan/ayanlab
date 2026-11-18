package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.TextMessage;
import com.ayanlab.pms.backend.repository.TextMessageRepository;
import com.ayanlab.pms.backend.service.ITextMessageService;
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
