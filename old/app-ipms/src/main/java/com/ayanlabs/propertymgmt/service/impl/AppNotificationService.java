package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.communication.AppNotification;
import com.ayanlabs.propertymgmt.repository.AppNotificationRepository;
import com.ayanlabs.propertymgmt.service.IAppNotificationService;
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
