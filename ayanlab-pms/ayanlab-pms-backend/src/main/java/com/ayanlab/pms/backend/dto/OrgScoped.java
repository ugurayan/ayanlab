package com.ayanlab.pms.backend.dto;

import java.util.UUID;

/** Marks IPMS entities scoped to a property-management organization. */
public interface OrgScoped {

    default UUID getOrganizationId() {
        try {
            java.lang.reflect.Field field = this.getClass().getDeclaredField("organizationId");
            field.setAccessible(true);
            Object value = field.get(this);
            return value instanceof UUID ? (UUID) value : null;
        } catch (Exception e) {
            return null;
        }
    }

    default void setOrganizationId(UUID organizationId) {
        try {
            java.lang.reflect.Field field = this.getClass().getDeclaredField("organizationId");
            field.setAccessible(true);
            field.set(this, organizationId);
        } catch (Exception ignored) {
        }
    }
}
