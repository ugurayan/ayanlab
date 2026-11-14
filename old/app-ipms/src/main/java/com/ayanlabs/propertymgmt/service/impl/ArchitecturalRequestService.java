package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.associations.ArchitecturalRequest;
import com.ayanlabs.propertymgmt.repository.ArchitecturalRequestRepository;
import com.ayanlabs.propertymgmt.service.IArchitecturalRequestService;
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
