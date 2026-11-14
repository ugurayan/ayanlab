package com.ayanlabs.propertymgmt.data.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupStep1Request(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Email String email,
    /** National number or E.164. */
    @NotBlank String phone,
    /** ISO 3166-1 alpha-2, e.g. US, TR. */
    @NotBlank String phoneCountryIso,
    /** Dial code without '+', e.g. 1, 90. Optional — derived when blank. */
    String phoneCountryCode,
    @NotBlank @Size(min = 8) String password
) {}
