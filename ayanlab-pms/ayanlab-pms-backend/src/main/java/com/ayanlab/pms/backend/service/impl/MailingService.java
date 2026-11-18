package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.Mailing;
import com.ayanlab.pms.backend.repository.MailingRepository;
import com.ayanlab.pms.backend.service.IMailingService;
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
