package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.pms.backend.data.enums.OrganizationJoinRequestStatusEnum;
import com.ayanlab.pms.backend.data.requests.*;
import com.ayanlab.pms.backend.data.responses.*;

import java.util.List;
import java.util.UUID;

public interface IIpmsSignupService {

    SignupStep1Response registerStep1(SignupStep1Request request, CoreContext context);

    SignupFieldAvailabilityResponse checkEmailAvailability(String email, CoreContext context);

    SignupFieldAvailabilityResponse checkPhoneAvailability(String phone, String phoneCountryIso, CoreContext context);

    IpmsSignupSessionResponse verifyEmail(SignupVerifyCodeRequest request, CoreContext context);

    IpmsSignupSessionResponse verifyMobile(SignupVerifyCodeRequest request, CoreContext context);

    IpmsSignupSessionResponse resendEmailVerification(CoreContext context);

    IpmsSignupSessionResponse resendMobileVerification(CoreContext context);

    IpmsSignupSessionResponse getSession(CoreContext context);

    IpmsSignupSessionResponse selectProfileType(SignupStep2ProfileTypeRequest request, CoreContext context);

    IpmsSignupSessionResponse configurePersonalAccount(SignupPersonalAccountRequest request, CoreContext context);

    IpmsSignupSessionResponse createNewOrganization(SignupNewOrganizationRequest request, CoreContext context);

    IpmsSignupSessionResponse joinByInviteCode(SignupJoinByInviteRequest request, CoreContext context);

    List<OrganizationSearchResult> searchOrganizations(String name, CoreContext context);

    IpmsSignupSessionResponse requestJoinOrganization(SignupJoinRequestRequest request, CoreContext context);

    SignupCompleteResponse complete(CoreContext context);

    OrganizationInviteResponse createOrganizationInvite(CreateOrganizationInviteRequest request, CoreContext context);

    List<OrganizationInviteResponse> listOrganizationInvites(CoreContext context);

    List<OrganizationJoinRequestResponse> listOrganizationJoinRequests(OrganizationJoinRequestStatusEnum status, CoreContext context);

    OrganizationJoinRequestResponse approveJoinRequest(UUID joinRequestId, ReviewJoinRequestRequest request, CoreContext context);

    OrganizationJoinRequestResponse rejectJoinRequest(UUID joinRequestId, ReviewJoinRequestRequest request, CoreContext context);
}
