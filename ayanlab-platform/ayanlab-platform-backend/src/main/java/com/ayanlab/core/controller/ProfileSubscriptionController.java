package com.ayanlab.core.controller;

import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.data.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.data.dto.SubscriptionDtos;
import com.ayanlab.core.data.requests.StartProfileSubscriptionRequest;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.entity.ProfileSubscription;
import com.ayanlab.core.service.IProfileSubscriptionService;
import com.ayanlab.core.service.ISubscriptionService;
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
@RequestMapping("/core/api/profile-subscriptions")
@Tag(name = "Core · ProfileSubscriptionController", description = "Profile-bound subscriptions")
@RequiredArgsConstructor
public class ProfileSubscriptionController extends BaseController {

    private static final String BASE_PATH = "/core/api/profile-subscriptions";

    private final IProfileSubscriptionService profileSubscriptionService;
    private final ISubscriptionService subscriptionService;

    @GetMapping(value = "catalog", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.SubscriptionPlanView>>> catalog(
            @RequestParam UUID appId,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        TracedCall call = tracedCall(log, "catalog-profileSubscriptions", "GET", BASE_PATH + "/catalog", headers, null, params);
        try {
            List<SubscriptionDtos.SubscriptionPlanView> rows = subscriptionService.listCatalog(appId);
            ResponseEntity<ApiResponse<List<SubscriptionDtos.SubscriptionPlanView>>> response = ok(rows, call.context());
            call.trace().success(Map.of("count", rows.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "start", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SubscriptionDtos.ProfileSubscriptionView>> start(
            @Valid @RequestBody StartProfileSubscriptionRequest request,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "start-profileSubscription", "POST", BASE_PATH + "/start", headers, request, null);
        try {
            SubscriptionDtos.ProfileSubscriptionView created = profileSubscriptionService.start(request);
            ResponseEntity<ApiResponse<SubscriptionDtos.ProfileSubscriptionView>> response =
                    createdBody(created, "Subscription started", call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.ProfileSubscriptionView>>> listByProfile(
            @RequestParam UUID profileId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("profileId", profileId);
        TracedCall call = tracedCall(log, "list-profileSubscriptions", "GET", BASE_PATH, headers, null, params);
        try {
            List<SubscriptionDtos.ProfileSubscriptionView> rows = subscriptionService.listProfileSubscriptions(profileId);
            ResponseEntity<ApiResponse<List<SubscriptionDtos.ProfileSubscriptionView>>> response = ok(rows, call.context());
            call.trace().success(Map.of("count", rows.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileSubscription>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-profileSubscription", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            ProfileSubscription found = profileSubscriptionService.findById(id, call.context());
            ResponseEntity<ApiResponse<ProfileSubscription>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
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
        TracedCall call = tracedCall(log, "delete-profileSubscription", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            profileSubscriptionService.hardDeleteById(id, call.context());
            } else {
            profileSubscriptionService.deleteById(id, call.context());
            }
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
