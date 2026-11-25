package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.dto.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.SubscriptionDtos;
import com.ayanlab.core.dto.request.StartOrganizationSubscriptionRequest;
import com.ayanlab.core.dto.request.StartUserSubscriptionRequest;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.core.service.ISubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController("ipmsSubscriptionController")
@RequestMapping("/pms/api/subscriptions")
@Tag(name = "IPMS · SubscriptionController", description = "Subscriptions (IPMS)")
@RequiredArgsConstructor
public class SubscriptionController extends BaseController {

    private static final String BASE_PATH = "/pms/api/subscriptions";

    private final ISubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.SubscriptionPlanView>>> plans(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-subscription-plans", "GET", BASE_PATH + "/plans", headers, null, null);
        try {
            List<SubscriptionDtos.SubscriptionPlanView> data = subscriptionService.listCatalog(requireAppId(headers));
            ResponseEntity<ApiResponse<List<SubscriptionDtos.SubscriptionPlanView>>> response = okBody(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.UserSubscriptionView>>> mySubscriptions(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-my-subscriptions", "GET", BASE_PATH + "/users/me", headers, null, null);
        try {
            UUID userId = requireUserId();
            List<SubscriptionDtos.UserSubscriptionView> data = subscriptionService.listUserSubscriptions(userId, requireAppId(headers));
            ResponseEntity<ApiResponse<List<SubscriptionDtos.UserSubscriptionView>>> response = okBody(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/users/me")
    public ResponseEntity<ApiResponse<SubscriptionDtos.UserSubscriptionView>> startMySubscription(
            @Valid @RequestBody StartUserSubscriptionRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "start-my-subscription", "POST", BASE_PATH + "/users/me", headers, body, null);
        try {
            UUID userId = requireUserId();
            SubscriptionDtos.UserSubscriptionView created = subscriptionService.startUserSubscription(userId, body);
            ResponseEntity<ApiResponse<SubscriptionDtos.UserSubscriptionView>> response =
                    createdBody(created, "Subscription started", call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.OrganizationSubscriptionView>>> organizationSubscriptions(
            @PathVariable UUID organizationId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("organizationId", organizationId);
        TracedCall call = tracedCall(log, "list-organization-subscriptions", "GET", BASE_PATH + "/organizations/{organizationId}", headers, null, params);
        try {
            assertCanReadOrganization(organizationId);
            List<SubscriptionDtos.OrganizationSubscriptionView> data =
                    subscriptionService.listOrganizationSubscriptions(organizationId);
            ResponseEntity<ApiResponse<List<SubscriptionDtos.OrganizationSubscriptionView>>> response = okBody(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/organizations/{organizationId}")
    @RequirePermission("ipms:subscriptions:manage")
    public ResponseEntity<ApiResponse<SubscriptionDtos.OrganizationSubscriptionView>> startOrganizationSubscription(
            @PathVariable UUID organizationId,
            @Valid @RequestBody StartOrganizationSubscriptionRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("organizationId", organizationId);
        TracedCall call = tracedCall(log, "start-organization-subscription", "POST", BASE_PATH + "/organizations/{organizationId}", headers, body, params);
        try {
            assertCanReadOrganization(organizationId);
            SubscriptionDtos.OrganizationSubscriptionView created =
                    subscriptionService.startOrganizationSubscription(organizationId, body);
            ResponseEntity<ApiResponse<SubscriptionDtos.OrganizationSubscriptionView>> response =
                    createdBody(created, "Subscription started", call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    private static UUID requireAppId(ClientHeadersPayload headers) {
        if (headers != null && headers.appId() != null) {
            return UUID.fromString(headers.appId());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-App-Id header is required");
    }

    private static UUID requireUserId() {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null || ctx.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ctx.getUserId();
    }

    private static void assertCanReadOrganization(UUID organizationId) {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        boolean sameOrg = ctx.getOrgId() != null && ctx.getOrgId().equals(organizationId);
        if (!sameOrg && !ctx.hasRole("ADMIN")) {
            throw new ForbiddenException("Not allowed to read subscriptions for this organization");
        }
    }
}
