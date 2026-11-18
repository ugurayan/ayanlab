package com.ayanlab.crm.backend.controller;

import com.ayanlab.core.data.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/crm/api")
@Tag(name = "CRM · AccountingController", description = "Accounting module APIs")
public class AccountingController extends BaseController {

    private static final String BASE_PATH = "/crm/api";

    @GetMapping("/accounts")
    @RequirePermission("crm:accounting:read")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAccounts(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-accounts", "GET", BASE_PATH + "/accounts", headers, null, null);
        try {
            List<Map<String, Object>> data = List.of();
            ResponseEntity<ApiResponse<List<Map<String, Object>>>> response =
                    okBody(data, "Accounting endpoint ready", call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "health", "GET", BASE_PATH + "/health", headers, null, null);
        try {
            String data = "Accounting module is running";
            ResponseEntity<ApiResponse<String>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
