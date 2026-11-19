package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.AccessControlEvent;
import com.ayanlab.pms.backend.service.IAccessControlEventService;
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
@RequestMapping("/pms/api/access-control-events")
@Tag(name = "IPMS · AccessControlEventController", description = "Access control event (TR module CRUD)")
@RequiredArgsConstructor
public class AccessControlEventController extends IpmsCrudController<AccessControlEvent> {

    private static final String BASE_PATH = "/pms/api/access-control-events";
    private final IAccessControlEventService accessControlEventService;

    @Override
    protected IAccessControlEventService service() {
        return accessControlEventService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "access-control-events";
    }

    @Override
    protected String entityLabel() {
        return "AccessControlEvent";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AccessControlEvent>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
