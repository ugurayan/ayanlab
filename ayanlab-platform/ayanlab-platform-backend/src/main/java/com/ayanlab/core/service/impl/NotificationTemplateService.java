package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.enums.NotificationTypeEnum;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.NotificationTemplateRepository;
import com.ayanlab.core.service.INotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService implements INotificationTemplateService {

    private final NotificationTemplateRepository repository;

    @Override
    @Transactional
    public NotificationTemplate create(NotificationTemplate item, CoreContext context) {
        item.setAppId(context.getAppId());
        normalizeAndValidate(item);
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
        if (item.getTemplateVersion() == null) {
            item.setTemplateVersion(current.getTemplateVersion());
        }
        if (item.getLanguage() == null) {
            item.setLanguage(current.getLanguage());
        }
        if (item.getType() == null) {
            item.setType(current.getType());
        }
        normalizeAndValidate(item);
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

    private void normalizeAndValidate(NotificationTemplate template) {
        if (template.getTemplateVersion() == null) {
            template.setTemplateVersion(1);
        }
        if (template.getLanguage() != null && template.getLanguage().isBlank()) {
            template.setLanguage(null);
        }
        if (template.getType() == null) {
            template.setType(NotificationTypeEnum.EMAIL);
        }
        if (template.getParametersJson() != null && template.getParametersJson().isBlank()) {
            template.setParametersJson(null);
        }
        if (template.getParametersJson() != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.readValue(template.getParametersJson(), mapper.getTypeFactory().constructCollectionType(List.class, com.ayanlab.core.dto.TemplateParameterDefinition.class));
            } catch (Exception ex) {
                throw new CoreException("VALIDATION_ERROR", "parametersJson is not a valid parameter definition array: " + ex.getMessage());
            }
        }
        if (template.getAppId() == null) {
            throw new CoreException("VALIDATION_ERROR", "appId is required");
        }
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        NotificationTemplate found = findById(id, context);
        if (found != null) {
            repository.delete(found);
        }
    }

    @Override
    @Transactional
    public NotificationTemplate setActive(UUID id, boolean active, CoreContext context) {
        NotificationTemplate found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "NotificationTemplate not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return repository.save(found);
    }
}

