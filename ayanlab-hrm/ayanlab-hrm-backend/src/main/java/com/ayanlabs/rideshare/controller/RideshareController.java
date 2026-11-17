package com.ayanlabs.rideshare.controller;

import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.annotation.RequireAppRole;
import com.ayanlabs.core.controller.BaseController;
import com.ayanlabs.core.data.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/hrm/api")
@Tag(name = "StaffIQ · RideshareController", description = "Rideshare endpoints (StaffIQ)")
public class RideshareController extends BaseController {

    private static final String BASE_PATH = "/hrm/api";

    @PostMapping("/rides/request")
    @RequireAppRole({"RIDER"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestRide(
            @RequestBody Map<String, Object> request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "request-ride", "POST", BASE_PATH + "/rides/request", headers, request, null);
        try {
            ResponseEntity<ApiResponse<Map<String, Object>>> response =
                    okBody(request, "Ride requested", call.context());
            call.trace().success(Map.of("success", true));
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
            String data = "Rideshare module is running";
            ResponseEntity<ApiResponse<String>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
