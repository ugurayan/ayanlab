package com.ayanlab.core.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApprovalDecisionRequest(
        @NotBlank String decisionNote
) {
}
