package com.ayanlab.pms.backend.authz;

import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.ayanlab.core.authz.catalog.AuthorizationCatalogs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpmsAuthorizationCatalogTest {

    @Test
    void catalogIsValidAndMatchesMockDemoUsers() throws Exception {
        AppAuthorizationCatalog catalog =
            AuthorizationCatalogs.load("classpath:authz/*.json", new ObjectMapper());
        assertThat(catalog.validate()).isEmpty();
        assertThat(catalog.appCode()).isEqualTo("ipms");
        assertThat(catalog.rolesOrEmpty()).extracting(AppAuthorizationCatalog.RoleSpec::code)
            .contains("FIELD_STAFF", "PROPERTY_MANAGER");
        assertThat(catalog.usersOrEmpty()).extracting(AppAuthorizationCatalog.UserSpec::email)
            .contains("pm@demo.ayanlab", "owner@demo.ayanlab", "field@demo.ayanlab");
    }
}