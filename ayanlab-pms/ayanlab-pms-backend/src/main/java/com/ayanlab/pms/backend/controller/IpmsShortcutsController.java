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
@RequestMapping("/pms/api/shortcuts")
@Tag(name = "IPMS · Shortcuts", description = "Buildium-style accounting shortcuts (B1.18)")
@Slf4j
public class IpmsShortcutsController extends BaseController {

  @PostMapping(value = "/management-fee", produces = MediaType.APPLICATION_JSON_VALUE)
  @RequirePermission("ipms:accounting:create")
  public ResponseEntity<ApiResponse<Map<String, Object>>> managementFee(
      @RequestBody(required = false) Map<String, Object> body,
      @ClientHeaders ClientHeadersPayload headers) {
    return stub("management-fee", body, headers);
  }

  @PostMapping(value = "/owner-draw", produces = MediaType.APPLICATION_JSON_VALUE)
  @RequirePermission("ipms:accounting:create")
  public ResponseEntity<ApiResponse<Map<String, Object>>> ownerDraw(
      @RequestBody(required = false) Map<String, Object> body,
      @ClientHeaders ClientHeadersPayload headers) {
    return stub("owner-draw", body, headers);
  }

  @PostMapping(value = "/passthrough", produces = MediaType.APPLICATION_JSON_VALUE)
  @RequirePermission("ipms:accounting:create")
  public ResponseEntity<ApiResponse<Map<String, Object>>> passthrough(
      @RequestBody(required = false) Map<String, Object> body,
      @ClientHeaders ClientHeadersPayload headers) {
    return stub("passthrough", body, headers);
  }

  private ResponseEntity<ApiResponse<Map<String, Object>>> stub(
      String type, Map<String, Object> body, ClientHeadersPayload headers) {
    TracedCall call =
        tracedCall(log, "shortcut-" + type, "POST", "/pms/api/shortcuts/" + type, headers, body, null);
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("jobId", UUID.randomUUID().toString());
    data.put("type", type);
    data.put("status", "QUEUED");
    data.put("message", "Shortcut stub — wire to GL posting in P2");
    return ok(data, call.context());
  }
}