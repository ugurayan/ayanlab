package com.ayanlab.crm.backend.controller;

import com.ayanlab.core.dto.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.provider.AuthorizationSnapshots;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController("accountingAuthorizationController")
@RequestMapping("/crm/api/authorization")
@Tag(name = "CRM · AuthorizationController", description = "Current principal / auth context (CRM / accounting)")
public class AuthorizationController extends BaseController {

    private static final String BASE_PATH = "/crm/api/authorization";

    @GetMapping("/context")
    public ResponseEntity<ApiResponse<Map<String, Object>>> context(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-authorization-context", "GET", BASE_PATH + "/context", headers, null, null);
        try {
            Map<String, Object> data = AuthorizationSnapshots.currentPrincipal();
            ResponseEntity<ApiResponse<Map<String, Object>>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
