package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.communication.AppNotification;
import com.ayanlab.pms.backend.repository.AppNotificationRepository;
import com.ayanlab.pms.backend.service.IAppNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppNotificationService extends AbstractIpmsOrgService<AppNotification, AppNotificationRepository> implements IAppNotificationService {

    public AppNotificationService(AppNotificationRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "AppNotification";
    }
}
