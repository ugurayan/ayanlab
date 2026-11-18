package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.dto.TemplateParameterDefinition;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.service.TemplateRenderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TemplateRenderServiceImpl implements TemplateRenderService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RenderedTemplate render(NotificationTemplate template, Map<String, Object> params, boolean strictValidation) {
        if (template == null) {
            throw new CoreException("INVALID_ARGUMENT", "Template is required");
        }
        Map<String, TemplateParameterDefinition> definitions = parseParameterDefinitions(template);
        Map<String, Object> safeParams = params != null ? params : Collections.emptyMap();

        validateParameters(definitions, safeParams, strictValidation);

        String renderedSubject = applyPlaceholders(template.getSubject(), safeParams);
        String renderedBody = applyPlaceholders(template.getBody(), safeParams);

        return new RenderedTemplate(renderedSubject, renderedBody);
    }

    @Override
    public Map<String, TemplateParameterDefinition> parseParameterDefinitions(NotificationTemplate template) {
        if (template.getParametersJson() == null || template.getParametersJson().isBlank()) {
            return Collections.emptyMap();
        }
        try {
            List<TemplateParameterDefinition> list = objectMapper.readValue(
                    template.getParametersJson(),
                    new TypeReference<List<TemplateParameterDefinition>>() {
                    });
            Map<String, TemplateParameterDefinition> map = new HashMap<>();
            for (TemplateParameterDefinition def : list) {
                map.put(def.name(), def);
            }
            return map;
        } catch (Exception ex) {
            throw new CoreException("VALIDATION_ERROR", "Failed to parse parametersJson: " + ex.getMessage());
        }
    }

    private void validateParameters(Map<String, TemplateParameterDefinition> definitions,
                                    Map<String, Object> params,
                                    boolean strictValidation) {
        for (TemplateParameterDefinition def : definitions.values()) {
            if (def.required()) {
                if (!params.containsKey(def.name()) || params.get(def.name()) == null) {
                    if (def.defaultValue() == null || def.defaultValue().isBlank()) {
                        throw new CoreException("VALIDATION_ERROR", "Missing required template parameter: " + def.name());
                    }
                }
            }
            if (params.containsKey(def.name()) && params.get(def.name()) != null) {
                Object value = params.get(def.name());
                String type = def.type().toUpperCase();
                switch (type) {
                    case "STRING" -> {
                        // any toString is fine
                    }
                    case "NUMBER" -> {
                        if (!(value instanceof Number)) {
                            throw new CoreException("VALIDATION_ERROR", "Parameter " + def.name() + " must be a number");
                        }
                    }
                    case "BOOLEAN" -> {
                        if (!(value instanceof Boolean)) {
                            throw new CoreException("VALIDATION_ERROR", "Parameter " + def.name() + " must be a boolean");
                        }
                    }
                    default -> {
                        // leave as string, or extend later
                    }
                }
            }
        }
        if (strictValidation) {
            for (String key : params.keySet()) {
                if (!definitions.containsKey(key)) {
                    throw new CoreException("VALIDATION_ERROR", "Unexpected template parameter: " + key);
                }
            }
        }
    }

    private String applyPlaceholders(String input, Map<String, Object> params) {
        if (input == null || input.isBlank()) {
            return input;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = params.get(key);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

