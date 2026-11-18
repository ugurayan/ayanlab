package com.ayanlab.core.data.requests;

import com.ayanlab.core.data.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.data.enums.ApprovalTypeEnum;
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
