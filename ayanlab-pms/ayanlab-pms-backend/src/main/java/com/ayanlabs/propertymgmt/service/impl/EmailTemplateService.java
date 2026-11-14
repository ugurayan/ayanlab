package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.EmailTemplate;
import com.ayanlabs.propertymgmt.repository.EmailTemplateRepository;
import com.ayanlabs.propertymgmt.service.IEmailTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailTemplateService extends AbstractIpmsOrgService<EmailTemplate, EmailTemplateRepository> implements IEmailTemplateService {

    public EmailTemplateService(EmailTemplateRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "EmailTemplate";
    }
}
