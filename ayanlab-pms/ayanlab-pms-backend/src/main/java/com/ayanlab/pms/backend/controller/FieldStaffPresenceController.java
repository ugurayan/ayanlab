package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.maintenance.FieldStaffPresence;
import com.ayanlab.pms.backend.service.IFieldStaffPresenceService;
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
@RequestMapping("/pms/api/field-staff-presence")
@Tag(name = "IPMS · FieldStaffPresenceController", description = "Field staff presence / availability")
@RequiredArgsConstructor
public class FieldStaffPresenceController extends IpmsCrudController<FieldStaffPresence> {

    private static final String BASE_PATH = "/pms/api/field-staff-presence";
    private final IFieldStaffPresenceService fieldStaffPresenceService;

    @Override
    protected IFieldStaffPresenceService service() {
        return fieldStaffPresenceService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "field-staff-presence";
    }

    @Override
    protected String entityLabel() {
        return "FieldStaffPresence";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<FieldStaffPresence>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
