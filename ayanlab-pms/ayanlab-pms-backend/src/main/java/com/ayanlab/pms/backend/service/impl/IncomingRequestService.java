package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tasks.IncomingRequest;
import com.ayanlab.pms.backend.repository.IncomingRequestRepository;
import com.ayanlab.pms.backend.service.IIncomingRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IncomingRequestService extends AbstractIpmsOrgService<IncomingRequest, IncomingRequestRepository> implements IIncomingRequestService {

    public IncomingRequestService(IncomingRequestRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "IncomingRequest";
    }
}
