package com.ayanlab.core.controller;

import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.enums.ApprovalStatusEnum;
import com.ayanlab.core.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.dto.request.ApprovalDecisionRequest;
import com.ayanlab.core.dto.request.CreateApprovalRequest;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.ApprovalRequest;
import com.ayanlab.core.service.IApprovalRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/approvals/requests")
@Tag(name = "Core · ApprovalController", description = "Generic approval workflows")
@RequiredArgsConstructor
public class ApprovalController extends BaseController {

    private static final String BASE_PATH = "/core/api/approvals/requests";

    private final IApprovalRequestService approvalRequestService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("core:approvals:manage")
    public ResponseEntity<ApiResponse<ApprovalRequest>> create(
            @Valid @RequestBody CreateApprovalRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-approval-request", "POST", BASE_PATH, headers, body, null);
        try {
            ApprovalRequest data = approvalRequestService.createRequest(body, call.context());
            call.trace().success(Map.of("createdId", data.getId(), "success", true));
            return createdBody(data, "Approval request created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission({"core:approvals:read", "core:approvals:manage"})
    public ResponseEntity<ApiResponse<List<ApprovalRequest>>> list(
            @RequestParam(value = "organizationId", required = false) UUID organizationId,
            @RequestParam(value = "subjectType", required = false) ApprovalSubjectTypeEnum subjectType,
            @RequestParam(value = "subjectId", required = false) UUID subjectId,
            @RequestParam(value = "status", required = false) ApprovalStatusEnum status,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("organizationId", organizationId);
        params.put("subjectType", subjectType);
        params.put("subjectId", subjectId);
        params.put("status", status);
        TracedCall call = tracedCall(log, "list-approval-requests", "GET", BASE_PATH, headers, null, params);
        try {
            List<ApprovalRequest> data = approvalRequestService.list(organizationId, subjectType, subjectId, status);
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("core:approvals:manage")
    public ResponseEntity<ApiResponse<ApprovalRequest>> approve(
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalDecisionRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        return applyDecision(id, body, headers, "approve-approval-request", "approve", approvalRequestService::approve);
    }

    @PostMapping(value = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("core:approvals:manage")
    public ResponseEntity<ApiResponse<ApprovalRequest>> reject(
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalDecisionRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        return applyDecision(id, body, headers, "reject-approval-request", "reject", approvalRequestService::reject);
    }

    @PostMapping(value = "/{id}/changes-requested", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("core:approvals:manage")
    public ResponseEntity<ApiResponse<ApprovalRequest>> changesRequested(
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalDecisionRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        return applyDecision(id, body, headers, "changes-requested-approval-request", "changes-requested", approvalRequestService::changesRequested);
    }

    private ResponseEntity<ApiResponse<ApprovalRequest>> applyDecision(
            UUID id,
            ApprovalDecisionRequest body,
            ClientHeadersPayload headers,
            String operation,
            String actionName,
            DecisionApplier applier) {
        Map<String, Object> params = Map.of("id", id);
        TracedCall call = tracedCall(log, operation, "POST", BASE_PATH + "/{id}/" + actionName, headers, body, params);
        try {
            ApprovalRequest data = applier.apply(id, body.decisionNote(), call.context());
            call.trace().success(Map.of("id", id, "status", data.getStatus(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @FunctionalInterface
    private interface DecisionApplier {
        ApprovalRequest apply(UUID id, String note, com.ayanlab.core.dto.CoreContext context);
    }
}
