package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.associations.ArchitecturalRequest;
import com.ayanlab.pms.backend.repository.ArchitecturalRequestRepository;
import com.ayanlab.pms.backend.service.IArchitecturalRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArchitecturalRequestService extends AbstractIpmsOrgService<ArchitecturalRequest, ArchitecturalRequestRepository> implements IArchitecturalRequestService {

    public ArchitecturalRequestService(ArchitecturalRequestRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "ArchitecturalRequest";
    }
}
