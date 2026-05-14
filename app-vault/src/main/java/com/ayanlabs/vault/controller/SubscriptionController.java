package com.ayanlabs.vault.controller;

import com.ayanlabs.core.annotation.RequireAppRole;
import com.ayanlabs.core.data.dto.SubscriptionDtos;
import com.ayanlabs.core.data.requests.StartOrganizationSubscriptionRequest;
import com.ayanlabs.core.data.requests.StartUserSubscriptionRequest;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.exception.ForbiddenException;
import com.ayanlabs.core.provider.AppSecurityContext;
import com.ayanlabs.core.service.ISubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController("vaultSubscriptionController")
@RequestMapping("/ayanlabs/api/vault/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final ISubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.SubscriptionPlanView>>> plans() {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.listActivePlans()));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.UserSubscriptionView>>> mySubscriptions(
            @RequestHeader(value = "X-App-Id", required = false) UUID appId) {
        UUID userId = requireUserId();
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.listUserSubscriptions(userId, appId)));
    }

    @PostMapping("/users/me")
    public ResponseEntity<ApiResponse<SubscriptionDtos.UserSubscriptionView>> startMySubscription(
            @Valid @RequestBody StartUserSubscriptionRequest body) {
        UUID userId = requireUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(subscriptionService.startUserSubscription(userId, body), "Subscription started"));
    }

    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<ApiResponse<List<SubscriptionDtos.OrganizationSubscriptionView>>> organizationSubscriptions(
            @PathVariable UUID organizationId) {
        assertCanReadOrganization(organizationId);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.listOrganizationSubscriptions(organizationId)));
    }

    @PostMapping("/organizations/{organizationId}")
    @RequireAppRole({"ADMIN"})
    public ResponseEntity<ApiResponse<SubscriptionDtos.OrganizationSubscriptionView>> startOrganizationSubscription(
            @PathVariable UUID organizationId,
            @Valid @RequestBody StartOrganizationSubscriptionRequest body) {
        assertCanReadOrganization(organizationId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(subscriptionService.startOrganizationSubscription(organizationId, body), "Subscription started"));
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
