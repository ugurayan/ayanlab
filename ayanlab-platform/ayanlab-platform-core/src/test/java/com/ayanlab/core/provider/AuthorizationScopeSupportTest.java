package com.ayanlab.core.provider;

import com.ayanlab.core.dto.ResourceScopeDto;
import com.ayanlab.core.enums.ResourceScopeTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import com.ayanlab.core.util.AuthorizationScopeSupport;
import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationScopeSupportTest {

    private final UUID propertyA = UUID.randomUUID();
    private final UUID propertyB = UUID.randomUUID();

    @AfterEach
    void clear() {
        AppSecurityContext.clear();
    }

    @Test
    void noContextGrantsNothing() {
        assertThat(AuthorizationScopeSupport.hasUnrestrictedAccess()).isFalse();
        assertThat(AuthorizationScopeSupport.canAccess(ResourceScopeTypeEnum.PROPERTY, propertyA)).isFalse();
        assertThat(AuthorizationScopeSupport.allowedResourceIds(ResourceScopeTypeEnum.PROPERTY)).isEmpty();
    }

    @Test
    void authenticatedWithoutScopesIsOrgWide() {
        AppSecurityContext.set(AppSecurityContext.AppContext.builder().userId(UUID.randomUUID()).build());

        assertThat(AuthorizationScopeSupport.hasUnrestrictedAccess()).isTrue();
        assertThat(AuthorizationScopeSupport.canAccess(ResourceScopeTypeEnum.PROPERTY, propertyA)).isTrue();
    }

    @Test
    void scopesFromTokenClaimRestrictAccess() {
        Object claim = ResourceScopeClaims.toClaim(List.of(ResourceScopeDto.builder()
            .resourceType(ResourceScopeTypeEnum.PROPERTY)
            .resourceId(propertyA)
            .build()));
        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .userId(UUID.randomUUID())
            .resourceScopes(ResourceScopeClaims.fromClaim(claim))
            .build());

        assertThat(AuthorizationScopeSupport.hasUnrestrictedAccess()).isFalse();
        assertThat(AuthorizationScopeSupport.canAccess(ResourceScopeTypeEnum.PROPERTY, propertyA)).isTrue();
        assertThat(AuthorizationScopeSupport.canAccess(ResourceScopeTypeEnum.PROPERTY, propertyB)).isFalse();
        assertThat(AuthorizationScopeSupport.allowedResourceIds(ResourceScopeTypeEnum.PROPERTY)).containsExactly(propertyA);
    }
}
