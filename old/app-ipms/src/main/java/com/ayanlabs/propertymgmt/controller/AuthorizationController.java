package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.controller.BaseController;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.provider.AuthorizationSnapshots;
import com.ayanlabs.propertymgmt.data.requests.AssignProfileRoleRequest;
import com.ayanlabs.propertymgmt.data.requests.CreateAuthRoleRequest;
import com.ayanlabs.propertymgmt.data.requests.UpdateMyProfileRequest;
import com.ayanlabs.propertymgmt.data.requests.UpdateOrgMemberRequest;
import com.ayanlabs.propertymgmt.data.responses.AuthPermissionResponse;
import com.ayanlabs.propertymgmt.data.responses.AuthRoleResponse;
import com.ayanlabs.propertymgmt.data.responses.MyProfileResponse;
import com.ayanlabs.propertymgmt.data.responses.OrgMemberResponse;
import com.ayanlabs.propertymgmt.service.IIpmsAuthorizationAdminService;
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
@RestController("propertyMgmtAuthorizationController")
@RequestMapping("/ayanlabs/api/property-mgmt/authorization")
@Tag(name = "IPMS · AuthorizationController", description = "Auth context + org authorization admin")
@RequiredArgsConstructor
public class AuthorizationController extends BaseController {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/authorization";

    private final IIpmsAuthorizationAdminService authorizationAdminService;

    @GetMapping("/context")
    public ResponseEntity<ApiResponse<Map<String, Object>>> context(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-authorization-context", "GET", BASE_PATH + "/context", headers, null, null);
        try {
            Map<String, Object> data = AuthorizationSnapshots.currentPrincipal();
            ResponseEntity<ApiResponse<Map<String, Object>>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrgMemberResponse>>> listUsers(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-org-members", "GET", BASE_PATH + "/users", headers, null, null);
        try {
            List<OrgMemberResponse> data = authorizationAdminService.listOrgMembers(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/users/{profileId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<OrgMemberResponse>> updateUser(
            @PathVariable UUID profileId,
            @Valid @RequestBody UpdateOrgMemberRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "update-org-member", "PUT", BASE_PATH + "/users/{profileId}", headers, request, Map.of("profileId", profileId));
        try {
            OrgMemberResponse data = authorizationAdminService.updateOrgMember(profileId, request, call.context());
            call.trace().success(Map.of("profileId", profileId, "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/users/{profileId}/roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<OrgMemberResponse>> assignRole(
            @PathVariable UUID profileId,
            @Valid @RequestBody AssignProfileRoleRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "assign-org-member-role", "POST", BASE_PATH + "/users/{profileId}/roles", headers, request, Map.of("profileId", profileId));
        try {
            OrgMemberResponse data = authorizationAdminService.assignRole(profileId, request, call.context());
            call.trace().success(Map.of("profileId", profileId, "roleId", request.roleId(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AuthRoleResponse>>> listRoles(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-auth-roles", "GET", BASE_PATH + "/roles", headers, null, null);
        try {
            List<AuthRoleResponse> data = authorizationAdminService.listRoles(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthRoleResponse>> createRole(
            @Valid @RequestBody CreateAuthRoleRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-auth-role", "POST", BASE_PATH + "/roles", headers, request, null);
        try {
            AuthRoleResponse data = authorizationAdminService.createRole(request, call.context());
            call.trace().success(Map.of("roleId", data.id(), "success", true));
            return createdBody(data, "Role created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AuthPermissionResponse>>> listPermissions(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-auth-permissions", "GET", BASE_PATH + "/permissions", headers, null, null);
        try {
            List<AuthPermissionResponse> data = authorizationAdminService.listPermissions(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/roles/{roleId}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AuthPermissionResponse>>> listRolePermissions(
            @PathVariable UUID roleId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-role-permissions", "GET", BASE_PATH + "/roles/{roleId}/permissions", headers, null, Map.of("roleId", roleId));
        try {
            List<AuthPermissionResponse> data = authorizationAdminService.listRolePermissions(roleId, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MyProfileResponse>> me(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-my-profile", "GET", BASE_PATH + "/me", headers, null, null);
        try {
            MyProfileResponse data = authorizationAdminService.getMyProfile(call.context());
            call.trace().success(Map.of("userId", data.userId(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MyProfileResponse>> updateMe(
            @Valid @RequestBody UpdateMyProfileRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "update-my-profile", "PUT", BASE_PATH + "/me", headers, request, null);
        try {
            MyProfileResponse data = authorizationAdminService.updateMyProfile(request, call.context());
            call.trace().success(Map.of("userId", data.userId(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
