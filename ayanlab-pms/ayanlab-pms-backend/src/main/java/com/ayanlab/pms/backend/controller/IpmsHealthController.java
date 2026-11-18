package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/pms/api")
@Tag(name = "IPMS · Health", description = "Property management module health")
public class IpmsHealthController extends BaseController {

    private static final String BASE_PATH = "/pms/api";

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "health", "GET", BASE_PATH + "/health", headers, null, null);
        try {
            String data = "Property Management module is running";
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
