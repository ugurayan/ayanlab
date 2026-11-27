package com.ayanlab.core.authz.cache;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import com.ayanlab.core.service.IAuthorizationResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ayanlabs.authz-cache", name = "source", havingValue = "local")
public class LocalProfileAuthzSource implements ProfileAuthzSource {

    private final IAuthorizationResolverService authorizationResolverService;

    @Override
    public EffectiveAuthorization load(CoreContext context) {
        if (context.getProfileId() != null) {
            return authorizationResolverService.resolveForProfile(context.getProfileId(), context);
        }
        return authorizationResolverService.resolveForCurrentProfile(context);
    }
}
