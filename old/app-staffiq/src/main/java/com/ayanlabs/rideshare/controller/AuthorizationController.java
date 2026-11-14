package com.ayanlabs.rideshare.controller;

import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.controller.BaseController;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.provider.AuthorizationSnapshots;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController("rideshareAuthorizationController")
@RequestMapping("/ayanlabs/api/rideshare/authorization")
@Tag(name = "StaffIQ · AuthorizationController", description = "Current principal / auth context (StaffIQ rideshare)")
public class AuthorizationController extends BaseController {

    private static final String BASE_PATH = "/ayanlabs/api/rideshare/authorization";

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
