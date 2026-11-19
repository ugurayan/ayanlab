package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.leasing.CommercialLeaseTerms;
import com.ayanlab.pms.backend.repository.CommercialLeaseTermsRepository;
import com.ayanlab.pms.backend.service.ICommercialLeaseTermsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommercialLeaseTermsService extends AbstractIpmsOrgService<CommercialLeaseTerms, CommercialLeaseTermsRepository> implements ICommercialLeaseTermsService {

    public CommercialLeaseTermsService(CommercialLeaseTermsRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "CommercialLeaseTerms";
    }
}
