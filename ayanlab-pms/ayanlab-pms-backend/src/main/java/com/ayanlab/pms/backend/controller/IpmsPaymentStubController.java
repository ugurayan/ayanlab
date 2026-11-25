package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pms/api/payments")
@Tag(name = "IPMS · Payments", description = "Payment provider abstraction stub (P2.13)")
@Slf4j
public class IpmsPaymentStubController extends BaseController {

    @PostMapping(value = "/intents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:accounting:create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createIntent(
            @RequestBody Map<String, Object> body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "payment-intent", "POST", "/pms/api/payments/intents", headers, body, null);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("provider", "STUB_STRIPE_ACH");
        data.put("intentId", "pi_stub_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        data.put("status", "requires_payment_method");
        data.put("amountCents", body == null ? 0 : body.getOrDefault("amountCents", 0));
        return ok(data, call.context());
    }
}