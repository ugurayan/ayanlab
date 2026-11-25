package com.ayanlab.core.controller;

import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.request.PreviewTemplateRequest;
import com.ayanlab.core.dto.request.SendTemplateEmailRequest;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.service.EmailDispatchService;
import com.ayanlab.core.service.INotificationTemplateService;
import com.ayanlab.core.service.TemplateRenderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/emails")
@Tag(name = "Core · EmailDispatchController", description = "Template-based email dispatch")
@RequiredArgsConstructor
public class EmailDispatchController extends BaseController {

    private static final String BASE_PATH = "/core/api/emails";

    private final EmailDispatchService emailDispatchService;
    private final INotificationTemplateService notificationTemplateService;
    private final TemplateRenderService templateRenderService;

    @PostMapping(value = "/send-template", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("core:email:manage")
    public ResponseEntity<ApiResponse<Boolean>> sendTemplate(
            @Valid @RequestBody SendTemplateEmailRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "send-template-email", "POST", BASE_PATH + "/send-template", headers, body, null);
        try {
            emailDispatchService.sendTemplateEmail(body, call.context());
            call.trace().success(Map.of("success", true));
            return okBody(true, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/preview-template", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("core:email:manage")
    public ResponseEntity<ApiResponse<Map<String, String>>> previewTemplate(
            @Valid @RequestBody PreviewTemplateRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "preview-template-email", "POST", BASE_PATH + "/preview-template", headers, body, null);
        try {
            CoreContext context = call.context();
            UUID appId = body.appId() != null ? body.appId() : context.getAppId();
            if (appId == null) {
                throw new com.ayanlab.core.exception.CoreException("VALIDATION_ERROR", "appId is required");
            }
            NotificationTemplate template = notificationTemplateService.findAll(
                    0,
                    1,
                    appId,
                    com.ayanlab.core.enums.NotificationTypeEnum.EMAIL,
                    body.templateCode(),
                    context
            ).getContent().stream().findFirst().orElse(null);
            if (template == null) {
                throw new com.ayanlab.core.exception.CoreException("NOT_FOUND", "Template not found");
            }
            TemplateRenderService.RenderedTemplate rendered =
                    templateRenderService.render(template, body.params(), body.strictValidation() == null || body.strictValidation());
            Map<String, String> data = Map.of(
                    "subject", rendered.subject(),
                    "body", rendered.body()
            );
            call.trace().success(Map.of("success", true));
            return okBody(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}

