package com.ayanlabs.propertymgmt.data.requests;

import com.ayanlabs.propertymgmt.data.enums.PersonalAccountTypeEnum;
import jakarta.validation.constraints.NotNull;

public record SignupPersonalAccountRequest(
    @NotNull PersonalAccountTypeEnum personalAccountType
) {}
