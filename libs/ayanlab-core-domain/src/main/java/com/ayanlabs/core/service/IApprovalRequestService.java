package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ApprovalStatusEnum;
import com.ayanlabs.core.data.enums.ApprovalSubjectTypeEnum;
import com.ayanlabs.core.data.requests.CreateApprovalRequest;
import com.ayanlabs.core.entity.ApprovalRequest;

import java.util.List;
import java.util.UUID;

public interface IApprovalRequestService extends IBaseService<ApprovalRequest> {

    ApprovalRequest createRequest(CreateApprovalRequest request, CoreContext context);

    List<ApprovalRequest> list(UUID organizationId, ApprovalSubjectTypeEnum subjectType, UUID subjectId, ApprovalStatusEnum status);

    ApprovalRequest approve(UUID id, String note, CoreContext context);

    ApprovalRequest reject(UUID id, String note, CoreContext context);

    ApprovalRequest changesRequested(UUID id, String note, CoreContext context);
}
