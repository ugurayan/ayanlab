package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.maintenance.FieldStaffPresence;
import com.ayanlabs.propertymgmt.repository.FieldStaffPresenceRepository;
import com.ayanlabs.propertymgmt.service.IFieldStaffPresenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FieldStaffPresenceService
        extends AbstractIpmsOrgService<FieldStaffPresence, FieldStaffPresenceRepository>
        implements IFieldStaffPresenceService {

    public FieldStaffPresenceService(FieldStaffPresenceRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "FieldStaffPresence";
    }
}
