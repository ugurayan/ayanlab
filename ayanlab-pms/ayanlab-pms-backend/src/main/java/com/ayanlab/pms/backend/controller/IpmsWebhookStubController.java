package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pms/api/webhooks")
@Tag(name = "IPMS · Webhook stubs", description = "Partner webhook ingress stubs (B1.15–17, B1.28)")
@Slf4j
public class IpmsWebhookStubController extends BaseController {

    @PostMapping(value = "/esign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:leasing:update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> esign(
            @RequestBody Map<String, Object> payload,
            @ClientHeaders ClientHeadersPayload headers) {
        return accepted("esign", payload, headers);
    }

    @PostMapping(value = "/screening", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:applicants:update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> screening(
            @RequestBody Map<String, Object> payload,
            @ClientHeaders ClientHeadersPayload headers) {
        return accepted("screening", payload, headers);
    }

    @PostMapping(value = "/pre-lease-payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:leasing:update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> preLeasePayment(
            @RequestBody Map<String, Object> payload,
            @ClientHeaders ClientHeadersPayload headers) {
        return accepted("pre-lease-payment", payload, headers);
    }

    @PostMapping(value = "/ivr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:communication:create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ivr(
            @RequestBody Map<String, Object> payload,
            @ClientHeaders ClientHeadersPayload headers) {
        return accepted("ivr", payload, headers);
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> accepted(
            String channel, Map<String, Object> payload, ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "webhook-" + channel, "POST", "/pms/api/webhooks/" + channel, headers, payload, null);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", UUID.randomUUID().toString());
        data.put("channel", channel);
        data.put("status", "ACCEPTED");
        data.put("receivedKeys", payload == null ? 0 : payload.size());
        return ok(data, call.context());
    }
}