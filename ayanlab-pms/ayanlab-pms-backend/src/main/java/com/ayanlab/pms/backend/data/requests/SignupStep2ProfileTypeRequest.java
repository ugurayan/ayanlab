package com.ayanlab.pms.backend.data.requests;

import com.ayanlab.pms.backend.data.enums.SignupAccountCategoryEnum;
import jakarta.validation.constraints.NotNull;

public record SignupStep2ProfileTypeRequest(
    @NotNull SignupAccountCategoryEnum accountCategory
) {}
