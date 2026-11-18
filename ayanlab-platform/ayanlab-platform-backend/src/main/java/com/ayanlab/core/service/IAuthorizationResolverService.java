package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.dto.EffectiveAuthorization;

import java.util.UUID;

public interface IAuthorizationResolverService {

    EffectiveAuthorization resolveForProfile(UUID profileId, CoreContext context);

    EffectiveAuthorization resolveForCurrentProfile(CoreContext context);
}
