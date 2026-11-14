package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.tasks.IncomingRequest;
import com.ayanlabs.propertymgmt.repository.IncomingRequestRepository;
import com.ayanlabs.propertymgmt.service.IIncomingRequestService;
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
