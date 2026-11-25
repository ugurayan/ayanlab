package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.dto.response.LeaseChargePortalResult;
import com.ayanlab.pms.backend.service.IIpmsLeaseChargePortalFlowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pms/api/flows")
@Tag(name = "IPMS · Flows", description = "Orchestrated flows (B2.4)")
@Slf4j
@RequiredArgsConstructor
public class IpmsFlowController extends BaseController {

    private static final String BASE = "/pms/api/flows";

    private final IIpmsLeaseChargePortalFlowService leaseChargePortalFlowService;

    @PostMapping(value = "/lease-charge-portal", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("ipms:leasing:manage")
    public ResponseEntity<ApiResponse<LeaseChargePortalResult>> leaseChargePortal(
            @RequestBody Map<String, Object> body,
            @ClientHeaders ClientHeadersPayload headers) {
        UUID leaseId = UUID.fromString(String.valueOf(body.get("leaseId")));
        Long cents = body.get("chargeAmountCents") == null ? null : Long.valueOf(String.valueOf(body.get("chargeAmountCents")));
        TracedCall call = tracedCall(log, "flow-lease-charge-portal", "POST", BASE + "/lease-charge-portal", headers, body, null);
        LeaseChargePortalResult data = leaseChargePortalFlowService.execute(call.context(), leaseId, cents);
        return ok(data, call.context());
    }
}