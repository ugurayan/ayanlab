package com.ayanlab.core.data.requests;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    /** National digits or E.164. Normalized to E.164 before persist. */
    @NotBlank String phone,
    /** ISO 3166-1 alpha-2 (US, TR). Required when phone is not E.164. */
    String phoneCountryIso,
    /** Optional dial code without '+' (1, 90). Derived from validated number when blank. */
    String phoneCountryCode,
    Boolean sendEmail,
    Boolean sendSms,
    String emailTemplateCode,
    String smsTemplateCode
) {}
