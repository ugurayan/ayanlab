package com.ayanlabs.core.service;

import com.ayanlabs.core.data.dto.TemplateParameterDefinition;
import com.ayanlabs.core.entity.NotificationTemplate;

import java.util.Map;

public interface TemplateRenderService {

    record RenderedTemplate(String subject, String body) {}

    RenderedTemplate render(NotificationTemplate template,
                            Map<String, Object> params,
                            boolean strictValidation);

    Map<String, TemplateParameterDefinition> parseParameterDefinitions(NotificationTemplate template);
}

