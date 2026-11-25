package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import com.ayanlab.core.dto.response.AccessSummary;
import com.ayanlab.core.entity.UserProfile;

import java.util.UUID;

public interface IAccessSummaryService {
    AccessSummary buildForProfile(UUID profileId, CoreContext context);

    AccessSummary build(EffectiveAuthorization authorization, UserProfile profile, String orgCodeHint);
}
