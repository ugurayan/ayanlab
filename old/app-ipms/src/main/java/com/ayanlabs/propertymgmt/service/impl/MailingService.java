package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.Mailing;
import com.ayanlabs.propertymgmt.repository.MailingRepository;
import com.ayanlabs.propertymgmt.service.IMailingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MailingService extends AbstractIpmsOrgService<Mailing, MailingRepository> implements IMailingService {

    public MailingService(MailingRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Mailing";
    }
}
