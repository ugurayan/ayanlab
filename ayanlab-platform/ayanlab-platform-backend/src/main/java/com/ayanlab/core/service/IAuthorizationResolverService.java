package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;

import java.util.UUID;

public interface IAuthorizationResolverService {

    EffectiveAuthorization resolveForProfile(UUID profileId, CoreContext context);

    EffectiveAuthorization resolveForCurrentProfile(CoreContext context);
}
