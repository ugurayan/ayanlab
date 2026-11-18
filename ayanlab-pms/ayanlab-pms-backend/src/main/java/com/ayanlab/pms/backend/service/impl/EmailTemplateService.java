package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.EmailTemplate;
import com.ayanlab.pms.backend.repository.EmailTemplateRepository;
import com.ayanlab.pms.backend.service.IEmailTemplateService;
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
