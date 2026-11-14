package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.CommunicationEmail;
import com.ayanlabs.propertymgmt.repository.CommunicationEmailRepository;
import com.ayanlabs.propertymgmt.service.ICommunicationEmailService;
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
