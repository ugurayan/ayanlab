package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.DebtInquiryCode;
import com.ayanlab.pms.backend.repository.DebtInquiryCodeRepository;
import com.ayanlab.pms.backend.service.IDebtInquiryCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DebtInquiryCodeService extends AbstractIpmsOrgService<DebtInquiryCode, DebtInquiryCodeRepository> implements IDebtInquiryCodeService {

    public DebtInquiryCodeService(DebtInquiryCodeRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "DebtInquiryCode";
    }
}
