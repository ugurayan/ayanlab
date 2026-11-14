package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.accounting.EftApproval;
import com.ayanlabs.propertymgmt.repository.EftApprovalRepository;
import com.ayanlabs.propertymgmt.service.IEftApprovalService;
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
