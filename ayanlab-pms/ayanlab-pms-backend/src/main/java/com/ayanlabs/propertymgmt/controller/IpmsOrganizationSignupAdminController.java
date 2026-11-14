package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.data.enums.OrganizationJoinRequestStatusEnum;
import com.ayanlabs.propertymgmt.data.requests.CreateOrganizationInviteRequest;
import com.ayanlabs.propertymgmt.data.requests.ReviewJoinRequestRequest;
import com.ayanlabs.propertymgmt.data.responses.OrganizationInviteResponse;
import com.ayanlabs.propertymgmt.data.responses.OrganizationJoinRequestResponse;
import com.ayanlabs.propertymgmt.service.IIpmsSignupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/ayanlabs/api/property-mgmt/signup/admin")
@Tag(name = "IPMS · SignupAdminController", description = "Organization admin signup tools (invites, join requests)")
@RequiredArgsConstructor
public class IpmsOrganizationSignupAdminController extends com.ayanlabs.core.controller.BaseController {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/signup/admin";

    private final IIpmsSignupService signupService;

    @PostMapping(value = "/invites", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<OrganizationInviteResponse>> createInvite(
            @Valid @RequestBody CreateOrganizationInviteRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-admin-create-invite", "POST", BASE_PATH + "/invites", headers, request, null);
        try {
            OrganizationInviteResponse data = signupService.createOrganizationInvite(request, call.context());
            call.trace().success(Map.of("inviteId", data.inviteId(), "success", true));
            return createdBody(data, "Invite code created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/invites", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrganizationInviteResponse>>> listInvites(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-admin-list-invites", "GET", BASE_PATH + "/invites", headers, null, null);
        try {
            List<OrganizationInviteResponse> data = signupService.listOrganizationInvites(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/join-requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrganizationJoinRequestResponse>>> listJoinRequests(
            @RequestParam(value = "status", required = false) OrganizationJoinRequestStatusEnum status,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-admin-list-join-requests", "GET", BASE_PATH + "/join-requests", headers, null, Map.of("status", status));
        try {
            List<OrganizationJoinRequestResponse> data = signupService.listOrganizationJoinRequests(status, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/join-requests/{joinRequestId}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<OrganizationJoinRequestResponse>> approveJoinRequest(
            @PathVariable UUID joinRequestId,
            @RequestBody(required = false) ReviewJoinRequestRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-admin-approve-join", "POST", BASE_PATH + "/join-requests/{joinRequestId}/approve", headers, request, Map.of("joinRequestId", joinRequestId));
        try {
            OrganizationJoinRequestResponse data = signupService.approveJoinRequest(
                    joinRequestId,
                    request != null ? request : new ReviewJoinRequestRequest(null),
                    call.context());
            call.trace().success(Map.of("joinRequestId", joinRequestId, "success", true));
            return okBody(data, "Join request approved", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/join-requests/{joinRequestId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<OrganizationJoinRequestResponse>> rejectJoinRequest(
            @PathVariable UUID joinRequestId,
            @RequestBody(required = false) ReviewJoinRequestRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-admin-reject-join", "POST", BASE_PATH + "/join-requests/{joinRequestId}/reject", headers, request, Map.of("joinRequestId", joinRequestId));
        try {
            OrganizationJoinRequestResponse data = signupService.rejectJoinRequest(
                    joinRequestId,
                    request != null ? request : new ReviewJoinRequestRequest(null),
                    call.context());
            call.trace().success(Map.of("joinRequestId", joinRequestId, "success", true));
            return okBody(data, "Join request rejected", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
