package com.ayanlab.pms.backend.security;

import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IpmsAuthzTest {

    @AfterEach
    void clear() {
        AppSecurityContext.clear();
    }

    @Test
    void requireOperation_allowsWildcardGrant() {
        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .permissions(Set.of("ipms:properties:*"))
            .build());
        assertThatCode(() -> IpmsAuthz.requireOperation("properties", "read")).doesNotThrowAnyException();
        assertThatCode(() -> IpmsAuthz.requireOperation("property-units", "update")).doesNotThrowAnyException();
    }

    @Test
    void requireOperation_mapsLeasingPrefixes() {
        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .permissions(Set.of("ipms:leasing:read"))
            .build());
        assertThatCode(() -> IpmsAuthz.requireOperation("leases", "read")).doesNotThrowAnyException();
    }

    @Test
    void requireOperation_deniesWithoutContext() {
        assertThatThrownBy(() -> IpmsAuthz.requireOperation("properties", "read"))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void requireOperation_deniesCrossResource() {
        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .permissions(Set.of("ipms:tenants:read"))
            .build());
        assertThatThrownBy(() -> IpmsAuthz.requireOperation("properties", "read"))
            .isInstanceOf(ForbiddenException.class);
    }
}