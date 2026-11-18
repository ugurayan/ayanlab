package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.communication.AutomatedEmailSetting;
import com.ayanlab.pms.backend.service.IAutomatedEmailSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pms/api/automated-email-settings")
@Tag(name = "IPMS · AutomatedEmailSettingController", description = "AutomatedEmailSetting CRUD")
@RequiredArgsConstructor
public class AutomatedEmailSettingController extends IpmsCrudController<AutomatedEmailSetting> {

    private static final String BASE_PATH = "/pms/api/automated-email-settings";
    private final IAutomatedEmailSettingService automatedEmailSettingService;

    @Override
    protected IAutomatedEmailSettingService service() {
        return automatedEmailSettingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "automated-email-settings";
    }

    @Override
    protected String entityLabel() {
        return "AutomatedEmailSetting";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AutomatedEmailSetting>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
