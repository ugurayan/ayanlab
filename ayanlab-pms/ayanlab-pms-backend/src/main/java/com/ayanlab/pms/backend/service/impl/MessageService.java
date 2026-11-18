package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.Message;
import com.ayanlab.pms.backend.repository.MessageRepository;
import com.ayanlab.pms.backend.service.IMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageService extends AbstractIpmsOrgService<Message, MessageRepository> implements IMessageService {

    public MessageService(MessageRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Message";
    }
}
