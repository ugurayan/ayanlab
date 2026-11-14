package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.data.requests.*;
import com.ayanlabs.propertymgmt.data.responses.*;
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

@Slf4j
@RestController
@RequestMapping("/ayanlabs/api/property-mgmt/signup")
@Tag(name = "IPMS · SignupController", description = "IPMS multi-step signup wizard")
@RequiredArgsConstructor
public class IpmsSignupController extends com.ayanlabs.core.controller.BaseController {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/signup";

    private final IIpmsSignupService signupService;

    @GetMapping(value = "/step-1/check-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SignupFieldAvailabilityResponse>> checkEmailAvailability(
            @RequestParam("email") String email,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-check-email", "GET", BASE_PATH + "/step-1/check-email", headers, null, Map.of("email", email));
        try {
            SignupFieldAvailabilityResponse data = signupService.checkEmailAvailability(email, call.context());
            call.trace().success(Map.of("available", data.available(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/step-1/check-phone", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SignupFieldAvailabilityResponse>> checkPhoneAvailability(
            @RequestParam("phone") String phone,
            @RequestParam(value = "phoneCountryIso", required = false) String phoneCountryIso,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-check-phone", "GET", BASE_PATH + "/step-1/check-phone", headers, null,
                Map.of("phone", phone, "phoneCountryIso", phoneCountryIso == null ? "" : phoneCountryIso));
        try {
            SignupFieldAvailabilityResponse data = signupService.checkPhoneAvailability(phone, phoneCountryIso, call.context());
            call.trace().success(Map.of("available", data.available(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-1/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SignupStep1Response>> registerStep1(
            @Valid @RequestBody SignupStep1Request request,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-step-1-register", "POST", BASE_PATH + "/step-1/register", headers, request, null);
        try {
            SignupStep1Response data = signupService.registerStep1(request, call.context());
            call.trace().success(Map.of("userId", data.userId(), "success", true));
            return createdBody(data, "User registered; verification codes sent", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-1/verify-email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> verifyEmail(
            @Valid @RequestBody SignupVerifyCodeRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-verify-email", "POST", BASE_PATH + "/step-1/verify-email", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.verifyEmail(request, call.context());
            call.trace().success(Map.of("emailVerified", data.emailVerified(), "success", true));
            return okBody(data, "Email verified", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-1/verify-mobile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> verifyMobile(
            @Valid @RequestBody SignupVerifyCodeRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-verify-mobile", "POST", BASE_PATH + "/step-1/verify-mobile", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.verifyMobile(request, call.context());
            call.trace().success(Map.of("mobileVerified", data.mobileVerified(), "success", true));
            return okBody(data, "Mobile verified", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-1/resend-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> resendEmail(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-resend-email", "POST", BASE_PATH + "/step-1/resend-email", headers, null, null);
        try {
            IpmsSignupSessionResponse data = signupService.resendEmailVerification(call.context());
            call.trace().success(Map.of("success", true));
            return okBody(data, "Email verification code resent", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-1/resend-mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> resendMobile(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-resend-mobile", "POST", BASE_PATH + "/step-1/resend-mobile", headers, null, null);
        try {
            IpmsSignupSessionResponse data = signupService.resendMobileVerification(call.context());
            call.trace().success(Map.of("success", true));
            return okBody(data, "Mobile verification code resent", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/session", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> getSession(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-get-session", "GET", BASE_PATH + "/session", headers, null, null);
        try {
            IpmsSignupSessionResponse data = signupService.getSession(call.context());
            call.trace().success(Map.of("currentStep", data.currentStep(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-2/profile-type", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> selectProfileType(
            @Valid @RequestBody SignupStep2ProfileTypeRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-step-2-profile-type", "POST", BASE_PATH + "/step-2/profile-type", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.selectProfileType(request, call.context());
            call.trace().success(Map.of("accountCategory", data.accountCategory(), "success", true));
            return okBody(data, "Profile type selected", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-3/personal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> configurePersonalAccount(
            @Valid @RequestBody SignupPersonalAccountRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-step-3-personal", "POST", BASE_PATH + "/step-3/personal", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.configurePersonalAccount(request, call.context());
            call.trace().success(Map.of("personalAccountType", data.personalAccountType(), "success", true));
            return okBody(data, "Personal account configured", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-3/organization/new-company", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> createNewOrganization(
            @Valid @RequestBody SignupNewOrganizationRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-step-3-org-new", "POST", BASE_PATH + "/step-3/organization/new-company", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.createNewOrganization(request, call.context());
            call.trace().success(Map.of("organizationId", data.organizationId(), "success", true));
            return okBody(data, "Organization created; you are company admin", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-3/organization/join/invite", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> joinByInvite(
            @Valid @RequestBody SignupJoinByInviteRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-step-3-org-invite", "POST", BASE_PATH + "/step-3/organization/join/invite", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.joinByInviteCode(request, call.context());
            call.trace().success(Map.of("organizationId", data.organizationId(), "success", true));
            return okBody(data, "Joined organization via invite code", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/step-3/organization/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrganizationSearchResult>>> searchOrganizations(
            @RequestParam("name") String name,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-org-search", "GET", BASE_PATH + "/step-3/organization/search", headers, null, Map.of("name", name));
        try {
            List<OrganizationSearchResult> data = signupService.searchOrganizations(name, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/step-3/organization/join/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsSignupSessionResponse>> requestJoinOrganization(
            @Valid @RequestBody SignupJoinRequestRequest request,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-step-3-org-join-request", "POST", BASE_PATH + "/step-3/organization/join/request", headers, request, null);
        try {
            IpmsSignupSessionResponse data = signupService.requestJoinOrganization(request, call.context());
            call.trace().success(Map.of("joinRequestId", data.joinRequestId(), "success", true));
            return okBody(data, "Join request sent to organization admin", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SignupCompleteResponse>> complete(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signup-complete", "POST", BASE_PATH + "/complete", headers, null, null);
        try {
            SignupCompleteResponse data = signupService.complete(call.context());
            call.trace().success(Map.of("redirectTo", data.redirectTo(), "success", true));
            return okBody(data, "Signup complete", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
