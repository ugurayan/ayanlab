package com.ayanlab.comm.backend.authz;

import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.ayanlab.core.authz.catalog.AuthorizationCatalogs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommAuthorizationCatalogTest {

    @Test
    void catalogIsValid() throws Exception {
        AppAuthorizationCatalog catalog = AuthorizationCatalogs.load(AuthorizationCatalogs.DEFAULT_LOCATION, new ObjectMapper());
        assertThat(catalog.appCode()).isEqualTo("comm");
        assertThat(catalog.validate()).isEmpty();
        assertThat(catalog.rolesOrEmpty()).extracting(AppAuthorizationCatalog.RoleSpec::code).contains("ADMIN");
    }
}
