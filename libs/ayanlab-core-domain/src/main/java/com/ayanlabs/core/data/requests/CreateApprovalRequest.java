package com.ayanlabs.core.data.requests;

import com.ayanlabs.core.data.enums.ApprovalSubjectTypeEnum;
import com.ayanlabs.core.data.enums.ApprovalTypeEnum;
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
