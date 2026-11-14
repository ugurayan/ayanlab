package com.ayanlabs.propertymgmt.entity.onboarding;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.enums.OnboardingStatusEnum;
import com.ayanlabs.propertymgmt.data.enums.OrganizationCompanyTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "IpmsOnboardingProfile",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_onboarding_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_onboarding_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpmsOnboardingProfile extends BaseEntity {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "adminUserId")
    private UUID adminUserId;

    @Column(name = "adminProfileId")
    private UUID adminProfileId;

    @NotBlank
    @Column(nullable = false, name = "companyLegalName")
    private String companyLegalName;

    @NotBlank
    @Column(nullable = false, name = "workspaceShortCode", length = 16)
    private String workspaceShortCode;

    @Column(nullable = false, name = "companyEmail")
    private String companyEmail;

    @Column(nullable = false, name = "companyPhone")
    private String companyPhone;

    @Column(name = "website")
    private String website;

    @Column(nullable = false, name = "addressLine1")
    private String addressLine1;

    @Column(nullable = false, name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipCode")
    private String zipCode;

    @Column(name = "country")
    private String country;

    @Column(nullable = false, name = "adminFirstName")
    private String adminFirstName;

    @Column(nullable = false, name = "adminLastName")
    private String adminLastName;

    @Column(nullable = false, name = "adminEmail")
    private String adminEmail;

    @Column(nullable = false, name = "adminPhone")
    private String adminPhone;

    @Builder.Default
    @Column(nullable = false, name = "currentStep")
    private Integer currentStep = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, name = "status")
    private OnboardingStatusEnum status = OnboardingStatusEnum.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "companyType")
    private OrganizationCompanyTypeEnum companyType;

    public static IpmsOnboardingProfileBuilder builder() {
        return new IpmsOnboardingProfileBuilder();
    }

    public static class IpmsOnboardingProfileBuilder {
        private final IpmsOnboardingProfile v = new IpmsOnboardingProfile();
        public IpmsOnboardingProfileBuilder organizationId(UUID x) { v.organizationId = x; return this; }
        public IpmsOnboardingProfileBuilder adminUserId(UUID x) { v.adminUserId = x; return this; }
        public IpmsOnboardingProfileBuilder adminProfileId(UUID x) { v.adminProfileId = x; return this; }
        public IpmsOnboardingProfileBuilder companyLegalName(String x) { v.companyLegalName = x; return this; }
        public IpmsOnboardingProfileBuilder workspaceShortCode(String x) { v.workspaceShortCode = x; return this; }
        public IpmsOnboardingProfileBuilder companyEmail(String x) { v.companyEmail = x; return this; }
        public IpmsOnboardingProfileBuilder companyPhone(String x) { v.companyPhone = x; return this; }
        public IpmsOnboardingProfileBuilder website(String x) { v.website = x; return this; }
        public IpmsOnboardingProfileBuilder addressLine1(String x) { v.addressLine1 = x; return this; }
        public IpmsOnboardingProfileBuilder city(String x) { v.city = x; return this; }
        public IpmsOnboardingProfileBuilder state(String x) { v.state = x; return this; }
        public IpmsOnboardingProfileBuilder zipCode(String x) { v.zipCode = x; return this; }
        public IpmsOnboardingProfileBuilder country(String x) { v.country = x; return this; }
        public IpmsOnboardingProfileBuilder adminFirstName(String x) { v.adminFirstName = x; return this; }
        public IpmsOnboardingProfileBuilder adminLastName(String x) { v.adminLastName = x; return this; }
        public IpmsOnboardingProfileBuilder adminEmail(String x) { v.adminEmail = x; return this; }
        public IpmsOnboardingProfileBuilder adminPhone(String x) { v.adminPhone = x; return this; }
        public IpmsOnboardingProfileBuilder currentStep(Integer x) { v.currentStep = x; return this; }
        public IpmsOnboardingProfileBuilder status(OnboardingStatusEnum x) { v.status = x; return this; }
        public IpmsOnboardingProfileBuilder companyType(OrganizationCompanyTypeEnum x) { v.companyType = x; return this; }
        public IpmsOnboardingProfile build() { return v; }
    }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getAdminUserId() { return adminUserId; }
    public void setAdminUserId(UUID adminUserId) { this.adminUserId = adminUserId; }
    public UUID getAdminProfileId() { return adminProfileId; }
    public void setAdminProfileId(UUID adminProfileId) { this.adminProfileId = adminProfileId; }
    public String getCompanyLegalName() { return companyLegalName; }
    public void setCompanyLegalName(String companyLegalName) { this.companyLegalName = companyLegalName; }
    public String getWorkspaceShortCode() { return workspaceShortCode; }
    public void setWorkspaceShortCode(String workspaceShortCode) { this.workspaceShortCode = workspaceShortCode; }
    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }
    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getAdminFirstName() { return adminFirstName; }
    public void setAdminFirstName(String adminFirstName) { this.adminFirstName = adminFirstName; }
    public String getAdminLastName() { return adminLastName; }
    public void setAdminLastName(String adminLastName) { this.adminLastName = adminLastName; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getAdminPhone() { return adminPhone; }
    public void setAdminPhone(String adminPhone) { this.adminPhone = adminPhone; }
    public Integer getCurrentStep() { return currentStep; }
    public void setCurrentStep(Integer currentStep) { this.currentStep = currentStep; }
    public OnboardingStatusEnum getStatus() { return status; }
    public void setStatus(OnboardingStatusEnum status) { this.status = status; }
    public OrganizationCompanyTypeEnum getCompanyType() { return companyType; }
    public void setCompanyType(OrganizationCompanyTypeEnum companyType) { this.companyType = companyType; }
}
