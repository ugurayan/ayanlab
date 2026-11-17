package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.SubscriptionPlan;
import com.ayanlabs.core.service.ISubscriptionPlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/core/api/subscription-plans")
@Tag(name = "Core · SubscriptionPlanController", description = "Subscription plan catalog (per app)")
@RequiredArgsConstructor
public class SubscriptionPlanController extends BaseController {

    private static final String BASE_PATH = "/core/api/subscription-plans";

    private final ISubscriptionPlanService subscriptionPlanService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlan>> create(
            @RequestBody SubscriptionPlan plan,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-subscriptionPlan", "POST", BASE_PATH, headers, plan, null);
        try {
            SubscriptionPlan created = subscriptionPlanService.create(plan, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlan>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlan>> update(
            @PathVariable UUID id,
            @RequestBody SubscriptionPlan plan,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-subscriptionPlan", "PUT", BASE_PATH + "/{id}", headers, plan, params);
        try {
            plan.setId(id);
            SubscriptionPlan updated = subscriptionPlanService.update(plan, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlan>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlan>> getById(
            @PathVariable UUID id,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-subscriptionPlan", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            SubscriptionPlan found = subscriptionPlanService.findById(id, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlan>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SubscriptionPlan>>> listByApp(
            @RequestParam("appId") UUID appId,
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("activeOnly", activeOnly);
        TracedCall call = tracedCall(log, "list-subscriptionPlans", "GET", BASE_PATH, headers, null, params);
        try {
            List<SubscriptionPlan> rows = subscriptionPlanService.listByApp(appId, activeOnly);
            ResponseEntity<ApiResponse<List<SubscriptionPlan>>> response = ok(rows, call.context());
            call.trace().success(Map.of("count", rows.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-subscriptionPlan", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            subscriptionPlanService.hardDeleteById(id, call.context());
            } else {
            subscriptionPlanService.deleteById(id, call.context());
            }
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}/activation")
    public ResponseEntity<ApiResponse<SubscriptionPlan>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            SubscriptionPlan updated = subscriptionPlanService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlan>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
