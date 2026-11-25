package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.communication.AppNotification;
import com.ayanlab.pms.backend.service.IAppNotificationService;
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
@RequestMapping("/pms/api/app-notifications")
@Tag(name = "IPMS · AppNotificationController", description = "AppNotification CRUD")
@RequiredArgsConstructor
public class AppNotificationController extends IpmsCrudController<AppNotification> {

    private static final String BASE_PATH = "/pms/api/app-notifications";
    private final IAppNotificationService appNotificationService;

    @Override
    protected IAppNotificationService service() {
        return appNotificationService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "app-notifications";
    }

    @Override
    protected String entityLabel() {
        return "AppNotification";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AppNotification>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
