package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String phone,
    Boolean sendEmail,
    Boolean sendSms,
    String emailTemplateCode,
    String smsTemplateCode
) {}
