package com.ayanlab.pms.backend.dto.request;

import com.ayanlab.pms.backend.enums.PersonalAccountTypeEnum;
import jakarta.validation.constraints.NotNull;

public record SignupPersonalAccountRequest(
    @NotNull PersonalAccountTypeEnum personalAccountType
) {}
