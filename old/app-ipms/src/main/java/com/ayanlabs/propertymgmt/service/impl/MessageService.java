package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.Message;
import com.ayanlabs.propertymgmt.repository.MessageRepository;
import com.ayanlabs.propertymgmt.service.IMessageService;
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
