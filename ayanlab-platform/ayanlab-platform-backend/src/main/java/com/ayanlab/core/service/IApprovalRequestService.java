package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ApprovalStatusEnum;
import com.ayanlab.core.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.dto.request.CreateApprovalRequest;
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
