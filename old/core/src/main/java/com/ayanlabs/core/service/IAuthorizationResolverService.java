package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.dto.EffectiveAuthorization;

import java.util.UUID;

public interface IAuthorizationResolverService {

    EffectiveAuthorization resolveForProfile(UUID profileId, CoreContext context);

    EffectiveAuthorization resolveForCurrentProfile(CoreContext context);
}
