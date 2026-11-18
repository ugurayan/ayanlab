package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.dto.EffectiveAuthorization;
import com.ayanlab.core.data.responses.AccessSummary;
import com.ayanlab.core.entity.UserProfile;

import java.util.UUID;

public interface IAccessSummaryService {
    AccessSummary buildForProfile(UUID profileId, CoreContext context);

    AccessSummary build(EffectiveAuthorization authorization, UserProfile profile, String orgCodeHint);
}
