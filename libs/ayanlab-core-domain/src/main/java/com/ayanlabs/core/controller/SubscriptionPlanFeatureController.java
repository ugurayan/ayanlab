package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.SubscriptionPlanFeature;
import com.ayanlabs.core.service.ISubscriptionPlanFeatureService;
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
@RestController
@RequestMapping("/api/core/subscription-plan-features")
@Tag(name = "Core · SubscriptionPlanFeatureController", description = "Plan feature bullets")
@RequiredArgsConstructor
public class SubscriptionPlanFeatureController extends BaseController {

    private static final String BASE_PATH = "/api/core/subscription-plan-features";

    private final ISubscriptionPlanFeatureService subscriptionPlanFeatureService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlanFeature>> create(
            @RequestBody SubscriptionPlanFeature feature,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-subscriptionPlanFeature", "POST", BASE_PATH, headers, feature, null);
        try {
            SubscriptionPlanFeature created = subscriptionPlanFeatureService.create(feature, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanFeature>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlanFeature>> update(
            @PathVariable UUID id,
            @RequestBody SubscriptionPlanFeature feature,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-subscriptionPlanFeature", "PUT", BASE_PATH + "/{id}", headers, feature, params);
        try {
            feature.setId(id);
            SubscriptionPlanFeature updated = subscriptionPlanFeatureService.update(feature, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanFeature>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlanFeature>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-subscriptionPlanFeature", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            SubscriptionPlanFeature found = subscriptionPlanFeatureService.findById(id, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanFeature>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SubscriptionPlanFeature>>> listByPlan(
            @RequestParam UUID subscriptionPlanId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("subscriptionPlanId", subscriptionPlanId);
        TracedCall call = tracedCall(log, "list-subscriptionPlanFeatures", "GET", BASE_PATH, headers, null, params);
        try {
            List<SubscriptionPlanFeature> rows = subscriptionPlanFeatureService.listByPlan(subscriptionPlanId);
            ResponseEntity<ApiResponse<List<SubscriptionPlanFeature>>> response = ok(rows, call.context());
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
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-subscriptionPlanFeature", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            subscriptionPlanFeatureService.deleteById(id, call.context());
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
