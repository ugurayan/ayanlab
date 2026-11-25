package com.ayanlab.core.dto.request;

import com.ayanlab.core.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.enums.ApprovalTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateApprovalRequest(
        @NotNull ApprovalSubjectTypeEnum subjectType,
        @NotNull UUID subjectId,
        UUID organizationId,
        @NotNull ApprovalTypeEnum approvalType,
        String decisionNote
) {
}
