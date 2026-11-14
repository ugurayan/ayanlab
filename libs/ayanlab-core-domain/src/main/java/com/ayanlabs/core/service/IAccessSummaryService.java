package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.dto.EffectiveAuthorization;
import com.ayanlabs.core.data.responses.AccessSummary;
import com.ayanlabs.core.entity.UserProfile;

import java.util.UUID;

public interface IAccessSummaryService {
    AccessSummary buildForProfile(UUID profileId, CoreContext context);

    AccessSummary build(EffectiveAuthorization authorization, UserProfile profile, String orgCodeHint);
}
