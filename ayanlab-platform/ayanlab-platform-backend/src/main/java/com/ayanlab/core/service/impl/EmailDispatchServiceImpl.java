package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.NotificationTypeEnum;
import com.ayanlab.core.data.requests.SendTemplateEmailRequest;
import com.ayanlab.core.entity.EmailDispatchLog;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.EmailDispatchLogRepository;
import com.ayanlab.core.repository.NotificationTemplateRepository;
import com.ayanlab.core.service.EmailDispatchService;
import com.ayanlab.core.service.TemplateRenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDispatchServiceImpl implements EmailDispatchService {

    private final NotificationTemplateRepository templateRepository;
    private final TemplateRenderService templateRenderService;
    private final com.ayanlab.core.service.support.IEmailSender emailSender;
    private final EmailDispatchLogRepository logRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void sendTemplateEmail(SendTemplateEmailRequest request, CoreContext context) {
        UUID appId = request.appId() != null ? request.appId() : context.getAppId();
        if (appId == null) {
            throw new CoreException("VALIDATION_ERROR", "appId is required for template email");
        }
        String language = request.language();
        NotificationTemplate template = resolveTemplate(appId, request.templateCode(), language);

        boolean strict = request.strictValidation() == null || Boolean.TRUE.equals(request.strictValidation());
        TemplateRenderService.RenderedTemplate rendered =
                templateRenderService.render(template, request.params(), strict);

        EmailDispatchLog logRow = new EmailDispatchLog();
        logRow.setAppId(appId);
        logRow.setToEmail(request.to());
        logRow.setTemplateId(template.getId());
        logRow.setTemplateCode(template.getCode());
        logRow.setRenderedSubject(rendered.subject());
        logRow.setStatus("PENDING");
        logRow.setSentAt(Instant.now());
        try {
            String payloadJson = objectMapper.writeValueAsString(Map.of(
                    "params", request.params(),
                    "language", language
            ));
            logRow.setRequestPayloadJson(payloadJson);
        } catch (Exception e) {
            log.debug("Failed to serialize email request payload: {}", e.getMessage());
        }
        logRow = logRepository.save(logRow);

        try {
            emailSender.send(request.to(), rendered.subject(), rendered.body());
            logRow.setStatus("SENT");
            logRow.setSentAt(Instant.now());
            logRepository.save(logRow);
        } catch (Exception ex) {
            logRow.setStatus("FAILED");
            logRow.setErrorMessage(ex.getMessage());
            logRow.setSentAt(Instant.now());
            logRepository.save(logRow);
            throw new CoreException("EMAIL_SEND_FAILED", "Failed to send email: " + ex.getMessage());
        }
    }

    private NotificationTemplate resolveTemplate(UUID appId, String code, String language) {
        List<NotificationTemplate> candidates;
        if (language != null && !language.isBlank()) {
            candidates = templateRepository.findByAppIdAndType(appId, NotificationTypeEnum.EMAIL, org.springframework.data.domain.Pageable.unpaged()).getContent();
            return candidates.stream()
                    .filter(t -> code.equals(t.getCode()) && language.equalsIgnoreCase(t.getLanguage()))
                    .findFirst()
                    .orElseGet(() -> fallbackTemplate(appId, code));
        }
        return fallbackTemplate(appId, code);
    }

    private NotificationTemplate fallbackTemplate(UUID appId, String code) {
        List<NotificationTemplate> candidates =
                templateRepository.findByAppIdAndType(appId, NotificationTypeEnum.EMAIL, org.springframework.data.domain.Pageable.unpaged()).getContent();
        return candidates.stream()
                .filter(t -> code.equals(t.getCode()))
                .findFirst()
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Email template not found for code=" + code));
    }
}

