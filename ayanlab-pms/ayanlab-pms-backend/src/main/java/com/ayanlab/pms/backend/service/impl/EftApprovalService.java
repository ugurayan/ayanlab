package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.accounting.EftApproval;
import com.ayanlab.pms.backend.repository.EftApprovalRepository;
import com.ayanlab.pms.backend.service.IEftApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EftApprovalService extends AbstractIpmsOrgService<EftApproval, EftApprovalRepository> implements IEftApprovalService {

    public EftApprovalService(EftApprovalRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "EftApproval";
    }
}
