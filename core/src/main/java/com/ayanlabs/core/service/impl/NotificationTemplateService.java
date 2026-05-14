package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.data.enums.NotificationTypeEnum;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.NotificationTemplate;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.NotificationTemplateRepository;
import com.ayanlabs.core.service.INotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService implements INotificationTemplateService {

    private final NotificationTemplateRepository repository;

    @Override
    @Transactional
    public NotificationTemplate create(NotificationTemplate item, CoreContext context) {
        item.setAppId(context.getAppId());
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public NotificationTemplate update(NotificationTemplate item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Template id required");
        NotificationTemplate current = repository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "Template not found");
        if (current.getAppId() != null && !current.getAppId().equals(context.getAppId()))
            throw new CoreException("FORBIDDEN", "Cannot modify template of a different app");
        item.setAppId(context.getAppId());
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public void delete(NotificationTemplate item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        repository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        NotificationTemplate t = findById(id, context);
        if (t != null) delete(t, context);
    }

    @Override
    public NotificationTemplate findById(UUID id, CoreContext context) {
        try {
            NotificationTemplate t = repository.findById(id).orElse(null);
            if (t == null) return null;
            if (t.getAppId() != null && !t.getAppId().equals(context.getAppId()))
                throw new CoreException("FORBIDDEN", "Template does not belong to the current app");
            return t;
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public PagedResponse<NotificationTemplate> findAll(int page, int size, UUID appId, NotificationTypeEnum type, String code, CoreContext context) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        org.springframework.data.domain.Page<NotificationTemplate> p;
        if (appId == null) appId = context.getAppId();
        if (appId == null) throw new CoreException("VALIDATION_ERROR", "appId is required");
        if (type != null) {
            p = repository.findByAppIdAndType(appId, type, pageable);
        } else if (code != null && !code.isBlank()) {
            p = repository.findByAppIdAndCodeContainingIgnoreCase(appId, code, pageable);
        } else {
            p = repository.findByAppId(appId, pageable);
        }
        PagedResponse.PagedResponseBuilder<NotificationTemplate> builder = PagedResponse.<NotificationTemplate>builder();
        return builder.content(p.getContent()).page(p.getNumber()).size(p.getSize()).totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast()).build();
    }
}

