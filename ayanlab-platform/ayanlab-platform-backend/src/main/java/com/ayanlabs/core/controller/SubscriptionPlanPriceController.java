package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.SubscriptionPlanPrice;
import com.ayanlabs.core.service.ISubscriptionPlanPriceService;
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
@RequestMapping("/core/api/subscription-plan-prices")
@Tag(name = "Core · SubscriptionPlanPriceController", description = "Plan billing variants (monthly / yearly)")
@RequiredArgsConstructor
public class SubscriptionPlanPriceController extends BaseController {

    private static final String BASE_PATH = "/core/api/subscription-plan-prices";

    private final ISubscriptionPlanPriceService subscriptionPlanPriceService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlanPrice>> create(
            @RequestBody SubscriptionPlanPrice price,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-subscriptionPlanPrice", "POST", BASE_PATH, headers, price, null);
        try {
            SubscriptionPlanPrice created = subscriptionPlanPriceService.create(price, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanPrice>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlanPrice>> update(
            @PathVariable UUID id,
            @RequestBody SubscriptionPlanPrice price,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-subscriptionPlanPrice", "PUT", BASE_PATH + "/{id}", headers, price, params);
        try {
            price.setId(id);
            SubscriptionPlanPrice updated = subscriptionPlanPriceService.update(price, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanPrice>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionPlanPrice>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-subscriptionPlanPrice", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            SubscriptionPlanPrice found = subscriptionPlanPriceService.findById(id, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanPrice>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SubscriptionPlanPrice>>> listByPlan(
            @RequestParam UUID subscriptionPlanId,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("subscriptionPlanId", subscriptionPlanId);
        params.put("activeOnly", activeOnly);
        TracedCall call = tracedCall(log, "list-subscriptionPlanPrices", "GET", BASE_PATH, headers, null, params);
        try {
            List<SubscriptionPlanPrice> rows = subscriptionPlanPriceService.listByPlan(subscriptionPlanId, activeOnly);
            ResponseEntity<ApiResponse<List<SubscriptionPlanPrice>>> response = ok(rows, call.context());
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
        TracedCall call = tracedCall(log, "delete-subscriptionPlanPrice", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            subscriptionPlanPriceService.hardDeleteById(id, call.context());
            } else {
            subscriptionPlanPriceService.deleteById(id, call.context());
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
    public ResponseEntity<ApiResponse<SubscriptionPlanPrice>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            SubscriptionPlanPrice updated = subscriptionPlanPriceService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<SubscriptionPlanPrice>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
