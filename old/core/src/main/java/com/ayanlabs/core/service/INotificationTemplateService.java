package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.NotificationTemplate;
import com.ayanlabs.core.data.enums.NotificationTypeEnum;

import java.util.UUID;

public interface INotificationTemplateService extends IBaseService<NotificationTemplate> {
    PagedResponse<NotificationTemplate> findAll(int page, int size, UUID appId, NotificationTypeEnum type, String code, CoreContext context);
}

