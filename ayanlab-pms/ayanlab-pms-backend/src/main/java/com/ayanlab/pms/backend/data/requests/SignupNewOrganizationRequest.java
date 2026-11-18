package com.ayanlab.pms.backend.data.requests;

import com.ayanlab.pms.backend.data.enums.OrganizationCompanyTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupNewOrganizationRequest(
    @NotNull OrganizationCompanyTypeEnum companyType,
    @NotBlank String companyLegalName,
    @NotBlank String workspaceShortCode,
    @NotBlank String companyEmail,
    @NotBlank String companyPhone,
    String website,
    @NotBlank String addressLine1,
    @NotBlank String city,
    String state,
    String zipCode,
    String country
) {}
