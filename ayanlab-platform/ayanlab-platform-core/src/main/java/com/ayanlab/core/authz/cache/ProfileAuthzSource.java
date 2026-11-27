package com.ayanlab.core.authz.cache;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;

public interface ProfileAuthzSource {

    EffectiveAuthorization load(CoreContext context);
}
