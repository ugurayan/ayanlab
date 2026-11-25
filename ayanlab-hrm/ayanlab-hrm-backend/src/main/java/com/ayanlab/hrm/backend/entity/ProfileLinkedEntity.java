package com.ayanlab.hrm.backend.entity;

import java.util.UUID;

/** Marker for entities keyed by platform profile with optional org/worker linkage. */
public interface ProfileLinkedEntity {

    UUID getProfileId();

    void setProfileId(UUID profileId);

    UUID getOrgId();

    void setOrgId(UUID orgId);

    UUID getWorkerId();

    void setWorkerId(UUID workerId);

    UUID getPlatformUserId();

    void setPlatformUserId(UUID platformUserId);
}
