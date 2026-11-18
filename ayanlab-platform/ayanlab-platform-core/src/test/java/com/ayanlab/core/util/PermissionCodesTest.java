package com.ayanlab.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionCodesTest {

    @Test
    void buildsThreeSegmentCode() {
        assertThat(PermissionCodes.of("staffiq", "shifts", "read")).isEqualTo("staffiq:shifts:read");
    }

    @Test
    void validatesConcreteCodes() {
        assertThat(PermissionCodes.isValid("staffiq:shifts:read")).isTrue();
        assertThat(PermissionCodes.isValid("ipms:lease.document:read")).isTrue();
        assertThat(PermissionCodes.isValid("staffiq:shifts:*")).isFalse();
        assertThat(PermissionCodes.isValid("staffiq:shifts:fly")).isFalse();
        assertThat(PermissionCodes.isValid("Staffiq:shifts:read")).isFalse();
        assertThat(PermissionCodes.isValid("ipms:leasing:leasing:read")).isFalse();
        assertThat(PermissionCodes.isValid("MANAGE_USERS")).isFalse();
        assertThat(PermissionCodes.isValid(null)).isFalse();
    }

    @Test
    void validatesGrantsWithWildcards() {
        assertThat(PermissionCodes.isValidGrant("staffiq:*:*")).isTrue();
        assertThat(PermissionCodes.isValidGrant("staffiq:shifts:*")).isTrue();
        assertThat(PermissionCodes.isValidGrant("staffiq:*:read")).isTrue();
        assertThat(PermissionCodes.isValidGrant("*:shifts:read")).isFalse();
    }

    @Test
    void convertsLegacyFourSegmentCodes() {
        assertThat(PermissionCodes.fromLegacy("ipms:leasing:leasing:read")).contains("ipms:leasing:read");
        assertThat(PermissionCodes.fromLegacy("staffiq:shifts:read")).contains("staffiq:shifts:read");
        assertThat(PermissionCodes.fromLegacy("MANAGE_USERS")).isEqualTo(Optional.empty());
    }

    @Test
    void exactGrantImpliesSameCode() {
        assertThat(PermissionCodes.implies("staffiq:shifts:read", "staffiq:shifts:read")).isTrue();
        assertThat(PermissionCodes.implies("staffiq:shifts:read", "staffiq:shifts:update")).isFalse();
    }

    @Test
    void wildcardsImplyActions() {
        assertThat(PermissionCodes.implies("staffiq:*:*", "staffiq:workers:update")).isTrue();
        assertThat(PermissionCodes.implies("staffiq:shifts:*", "staffiq:shifts:delete")).isTrue();
        assertThat(PermissionCodes.implies("staffiq:*:read", "staffiq:finance:read")).isTrue();
    }

    @Test
    void manageIsAnOrdinaryAction() {
        assertThat(PermissionCodes.implies("staffiq:shifts:manage", "staffiq:shifts:manage")).isTrue();
        assertThat(PermissionCodes.implies("staffiq:shifts:manage", "staffiq:shifts:delete")).isFalse();
    }

    @Test
    void grantsNeverCrossAppsOrResources() {
        assertThat(PermissionCodes.implies("staffiq:*:*", "crm:clients:read")).isFalse();
        assertThat(PermissionCodes.implies("staffiq:shifts:*", "staffiq:workers:read")).isFalse();
        assertThat(PermissionCodes.implies("staffiq:*:read", "staffiq:finance:update")).isFalse();
    }

    @Test
    void anyImpliesAcceptsLegacyRequiredCode() {
        List<String> grants = List.of("ipms:leasing:read");
        assertThat(PermissionCodes.anyImplies(grants, "ipms:leasing:leasing:read")).isTrue();
        assertThat(PermissionCodes.anyImplies(grants, "ipms:leasing:update")).isFalse();
        assertThat(PermissionCodes.anyImplies(null, "ipms:leasing:read")).isFalse();
    }
}
