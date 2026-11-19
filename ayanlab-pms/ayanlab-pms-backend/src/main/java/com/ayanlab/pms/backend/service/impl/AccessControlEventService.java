package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tr.AccessControlEvent;
import com.ayanlab.pms.backend.repository.AccessControlEventRepository;
import com.ayanlab.pms.backend.service.IAccessControlEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccessControlEventService extends AbstractIpmsOrgService<AccessControlEvent, AccessControlEventRepository> implements IAccessControlEventService {

    public AccessControlEventService(AccessControlEventRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "AccessControlEvent";
    }
}
