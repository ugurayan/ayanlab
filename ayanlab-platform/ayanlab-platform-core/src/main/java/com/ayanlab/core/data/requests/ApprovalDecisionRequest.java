package com.ayanlab.core.data.requests;

import jakarta.validation.constraints.NotBlank;

public record ApprovalDecisionRequest(
        @NotBlank String decisionNote
) {
}
