package com.ayanlab.core.service;

import com.ayanlab.core.data.dto.TemplateParameterDefinition;
import com.ayanlab.core.entity.NotificationTemplate;

import java.util.Map;

public interface TemplateRenderService {

    record RenderedTemplate(String subject, String body) {}

    RenderedTemplate render(NotificationTemplate template,
                            Map<String, Object> params,
                            boolean strictValidation);

    Map<String, TemplateParameterDefinition> parseParameterDefinitions(NotificationTemplate template);
}

