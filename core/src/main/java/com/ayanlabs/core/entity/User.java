package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.EmailStatusEnum;
import com.ayanlabs.core.data.enums.MobileStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "User", schema = "core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @JsonProperty("firstName")
    @Column(nullable = false, name = "firstName")
    private String firstName;

    @JsonProperty("lastName")
    @Column(nullable = false, name = "lastName")
    private String lastName;


    @JsonProperty("avatarUrl")
    @Column(nullable = true, name = "avatarUrl")
    private String avatarUrl;

    @JsonProperty("birthDate")
    @Column(name = "birthDate")
    private String birthDate;                   // MM/DD/YYYY

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appId", nullable = false)
//    private App app;
    @Column(nullable = false, name = "appId")
    private UUID appId;

    // Email Details ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    @JsonProperty("email")
    @Column(name = "email", nullable = false)
    private String email;

    @JsonProperty("isEmailVerified")
    @Column(name = "isEmailVerified")
    private Boolean isEmailVerified;

    @JsonProperty("emailVerificationDate")
    @Column(name = "emailVerificationDate")
    private Instant emailVerificationDate;

    @JsonProperty("emailConfirmationCode")
    @Column(name = "emailConfirmationCode")
    private String emailConfirmationCode;

    @JsonProperty("emailCodeExpiration")
    @Column(name = "emailCodeExpiration")
    private Instant emailCodeExpiration;

    @JsonProperty("emailCodeStatus")
    @Enumerated(EnumType.STRING)
    @Column(name = "emailCodeStatus")
    private EmailStatusEnum emailCodeStatus;

    @JsonProperty("isEmailCommunicationValid")
    @Column(name = "isEmailCommunicationValid")
    private Boolean isEmailCommunicationValid;

    @JsonProperty("emailAlternative")
    @Column(name = "emailAlternative")
    private String emailAlternative;

    // Mobile Details ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    @JsonProperty("mobile")
    @Column(name = "mobile", nullable = true)
    private String mobile;

    @JsonProperty("isMobileVerified")
    @Column(name = "isMobileVerified")
    private Boolean isMobileVerified;

    @JsonProperty("mobileVerificationDate")
    @Column(name = "mobileVerificationDate")
    private Long mobileVerificationDate;

    @JsonProperty("mobileVerificationCode")
    @Column(name = "mobileVerificationCode")
    private String mobileVerificationCode;

    @JsonProperty("mobileCodeExpiration")
    @Column(name = "mobileCodeExpiration")
    private Instant mobileCodeExpiration;

    @JsonProperty("mobileCodeStatus")
    @Enumerated(EnumType.STRING)
    @Column(name = "mobileCodeStatus")
    private MobileStatusEnum mobileCodeStatus;

    @JsonProperty("isMobileCommunicationValid")
    @Column(name = "isMobileCommunicationValid")
    private Boolean isMobileCommunicationValid;

    @JsonProperty("mobileAlternative")
    @Column(name = "mobileAlternative", unique = false)
    private String mobileAlternative;

    // PASSWORD & MFA ----------------------------------------------

    @JsonProperty("password")
    @Column(nullable = false, name = "password")
    private String password;

    @JsonProperty("passwordExpirationDate")
    @Column(name = "passwordExpirationDate")
    private Instant passwordExpirationDate;

    @Builder.Default
    @JsonProperty("mfaEnabled")
    @Column(name = "mfaEnabled")
    private boolean mfaEnabled = false;

    @JsonProperty("mfaSecret")
    @Column(name = "mfaSecret")
    private String mfaSecret;

    @JsonProperty("lastLoginAt")
    @Column(name = "lastLoginAt")
    private Instant lastLoginAt;

    // Password reset fields
    @JsonProperty("passwordResetToken")
    @Column(name = "passwordResetToken")
    private String passwordResetToken;

    @JsonProperty("passwordResetExpiration")
    @Column(name = "passwordResetExpiration")
    private Instant passwordResetExpiration;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
