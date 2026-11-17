package com.ayanlabs.core.data.enums;

/**
 * How an application is sold / entitled: organization-only, user-only, or both.
 * Migrated from legacy {@code SubscriptionEnum} (tmp/auth Applications.json).
 */
public enum AppSubscriptionModeEnum {
    /** Entitlement at organization level (e.g. vault org plan, geofencing). */
    ORGANIZATION,
    /** Entitlement per user / profile (single-tenant consumer apps). */
    USER,
    /** Both org and user-level plans (PMS, CRM, finance). */
    ORGANIZATION_AND_USER
}