package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.enums.NotificationTypeEnum;

import java.util.UUID;

public interface INotificationTemplateService extends IBaseService<NotificationTemplate> {
    PagedResponse<NotificationTemplate> findAll(int page, int size, UUID appId, NotificationTypeEnum type, String code, CoreContext context);
}

