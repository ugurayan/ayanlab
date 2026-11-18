package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.CommunicationEmail;
import com.ayanlab.pms.backend.repository.CommunicationEmailRepository;
import com.ayanlab.pms.backend.service.ICommunicationEmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunicationEmailService extends AbstractIpmsOrgService<CommunicationEmail, CommunicationEmailRepository> implements ICommunicationEmailService {

    public CommunicationEmailService(CommunicationEmailRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "CommunicationEmail";
    }
}
