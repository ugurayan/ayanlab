package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.maintenance.FieldStaffPresence;
import com.ayanlab.pms.backend.repository.FieldStaffPresenceRepository;
import com.ayanlab.pms.backend.service.IFieldStaffPresenceService;
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
