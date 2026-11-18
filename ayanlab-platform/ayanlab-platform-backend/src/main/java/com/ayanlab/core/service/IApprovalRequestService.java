package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ApprovalStatusEnum;
import com.ayanlab.core.data.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.data.requests.CreateApprovalRequest;
import com.ayanlab.core.entity.ApprovalRequest;

import java.util.List;
import java.util.UUID;

public interface IApprovalRequestService extends IBaseService<ApprovalRequest> {

    ApprovalRequest createRequest(CreateApprovalRequest request, CoreContext context);

    List<ApprovalRequest> list(UUID organizationId, ApprovalSubjectTypeEnum subjectType, UUID subjectId, ApprovalStatusEnum status);

    ApprovalRequest approve(UUID id, String note, CoreContext context);

    ApprovalRequest reject(UUID id, String note, CoreContext context);

    ApprovalRequest changesRequested(UUID id, String note, CoreContext context);
}
