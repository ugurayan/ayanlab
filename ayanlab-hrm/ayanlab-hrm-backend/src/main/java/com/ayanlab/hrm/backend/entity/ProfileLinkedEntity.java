package com.ayanlab.hrm.backend.entity;

/** Marker for entities keyed by platform profile with optional org/worker linkage. */
public interface ProfileLinkedEntity {

    String getProfileId();

    void setProfileId(String profileId);

    String getOrgId();

    void setOrgId(String orgId);

    String getWorkerId();

    void setWorkerId(String workerId);

    String getPlatformUserId();

    void setPlatformUserId(String platformUserId);
}