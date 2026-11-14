-- Reference DDL for core subscription catalog (Hibernate ddl-auto may apply entities directly).
-- PostgreSQL · schema: core

CREATE SCHEMA IF NOT EXISTS core;

-- Plan tier per application (Individual, Families, Free, …)
CREATE TABLE IF NOT EXISTS core."SubscriptionPlan" (
    id                  UUID PRIMARY KEY,
    "appId"             UUID NOT NULL,
    code                VARCHAR(64) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    "sortOrder"         INTEGER DEFAULT 0,
    "isFree"            BOOLEAN NOT NULL DEFAULT FALSE,
    "inheritsFromPlanId" UUID,
    "pricingUnit"       VARCHAR(32) DEFAULT 'FLAT',
    "maxSeats"          INTEGER,
    "trialDays"         INTEGER DEFAULT 0,
    "catalogActive"     BOOLEAN NOT NULL DEFAULT TRUE,
    "createdAt"         TIMESTAMPTZ,
    "updatedAt"         TIMESTAMPTZ,
    "createdBy"         UUID,
    "updatedBy"         UUID,
    "isActive"          BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_sub_plan_app_code UNIQUE ("appId", code)
);

CREATE INDEX IF NOT EXISTS idx_sub_plan_app ON core."SubscriptionPlan" ("appId");

-- Monthly / yearly / lifetime SKU rows (1Password-style dual billing)
CREATE TABLE IF NOT EXISTS core."SubscriptionPlanPrice" (
    id                      UUID PRIMARY KEY,
    "subscriptionPlanId"    UUID NOT NULL REFERENCES core."SubscriptionPlan"(id),
    "billingPeriod"         VARCHAR(32) NOT NULL,
    "amountCents"           BIGINT,
    "listAmountCents"       BIGINT,
    currency                VARCHAR(8) DEFAULT 'USD',
    "appleStoreProductId"   VARCHAR(255),
    "googlePlayProductId"   VARCHAR(255),
    "catalogActive"         BOOLEAN NOT NULL DEFAULT TRUE,
    "createdAt"             TIMESTAMPTZ,
    "updatedAt"             TIMESTAMPTZ,
    "createdBy"             UUID,
    "updatedBy"             UUID,
    "isActive"              BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_sub_plan_price_period UNIQUE ("subscriptionPlanId", "billingPeriod")
);

-- Feature bullets (included / comparison)
CREATE TABLE IF NOT EXISTS core."SubscriptionPlanFeature" (
    id                      UUID PRIMARY KEY,
    "subscriptionPlanId"    UUID NOT NULL REFERENCES core."SubscriptionPlan"(id),
    "featureCode"           VARCHAR(128) NOT NULL,
    title                   VARCHAR(512) NOT NULL,
    description             TEXT,
    "sortOrder"             INTEGER DEFAULT 0,
    included                BOOLEAN NOT NULL DEFAULT TRUE,
    "createdAt"             TIMESTAMPTZ,
    "updatedAt"             TIMESTAMPTZ,
    "createdBy"             UUID,
    "updatedBy"             UUID,
    "isActive"              BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_sub_plan_feature_plan ON core."SubscriptionPlanFeature" ("subscriptionPlanId");

-- Profile-bound subscription instance
CREATE TABLE IF NOT EXISTS core."ProfileSubscription" (
    id                          UUID PRIMARY KEY,
    "profileId"                   UUID NOT NULL,
    "subscriptionPlanId"        UUID NOT NULL REFERENCES core."SubscriptionPlan"(id),
    "subscriptionPlanPriceId"   UUID REFERENCES core."SubscriptionPlanPrice"(id),
    status                      VARCHAR(32) NOT NULL,
    "startedAt"                 TIMESTAMPTZ NOT NULL,
    "currentPeriodStart"        TIMESTAMPTZ,
    "currentPeriodEnd"          TIMESTAMPTZ,
    "trialEndsAt"               TIMESTAMPTZ,
    "cancelledAt"               TIMESTAMPTZ,
    "autoRenew"                 BOOLEAN DEFAULT TRUE,
    "externalSubscriptionId"    VARCHAR(512),
    "createdAt"                 TIMESTAMPTZ,
    "updatedAt"                 TIMESTAMPTZ,
    "createdBy"                 UUID,
    "updatedBy"                 UUID,
    "isActive"                  BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_profile_sub_profile ON core."ProfileSubscription" ("profileId");
CREATE INDEX IF NOT EXISTS idx_profile_sub_status ON core."ProfileSubscription" ("profileId", status);

-- Organization-level subscription (B2B)
CREATE TABLE IF NOT EXISTS core."OrganizationSubscription" (
    id                          UUID PRIMARY KEY,
    "organizationId"            UUID NOT NULL,
    "appId"                     UUID,
    "subscriptionPlanId"        UUID NOT NULL REFERENCES core."SubscriptionPlan"(id),
    "subscriptionPlanPriceId"   UUID REFERENCES core."SubscriptionPlanPrice"(id),
    status                      VARCHAR(32) NOT NULL,
    "startedAt"                 TIMESTAMPTZ NOT NULL,
    "currentPeriodStart"        TIMESTAMPTZ,
    "currentPeriodEnd"          TIMESTAMPTZ,
    "trialEndsAt"               TIMESTAMPTZ,
    "cancelledAt"               TIMESTAMPTZ,
    "autoRenew"                 BOOLEAN DEFAULT TRUE,
    "externalSubscriptionId"    VARCHAR(512),
    "createdAt"                 TIMESTAMPTZ,
    "updatedAt"                 TIMESTAMPTZ,
    "createdBy"                 UUID,
    "updatedBy"                 UUID,
    "isActive"                  BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_org_sub_org_app ON core."OrganizationSubscription" ("organizationId", "appId");
