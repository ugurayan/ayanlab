package com.ayanlab.pms.backend.data.requests;

import com.ayanlab.pms.backend.data.enums.PersonalAccountTypeEnum;
import jakarta.validation.constraints.NotNull;

public record SignupPersonalAccountRequest(
    @NotNull PersonalAccountTypeEnum personalAccountType
) {}
