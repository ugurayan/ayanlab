package com.ayanlab.pms.backend.dto.request;

import com.ayanlab.pms.backend.enums.SignupAccountCategoryEnum;
import jakarta.validation.constraints.NotNull;

public record SignupStep2ProfileTypeRequest(
    @NotNull SignupAccountCategoryEnum accountCategory
) {}
