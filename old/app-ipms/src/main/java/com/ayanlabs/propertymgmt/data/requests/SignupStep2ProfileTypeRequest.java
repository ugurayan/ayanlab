package com.ayanlabs.propertymgmt.data.requests;

import com.ayanlabs.propertymgmt.data.enums.SignupAccountCategoryEnum;
import jakarta.validation.constraints.NotNull;

public record SignupStep2ProfileTypeRequest(
    @NotNull SignupAccountCategoryEnum accountCategory
) {}
