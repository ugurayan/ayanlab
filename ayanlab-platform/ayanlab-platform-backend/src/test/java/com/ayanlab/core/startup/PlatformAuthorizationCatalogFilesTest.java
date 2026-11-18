package com.ayanlab.core.startup;

import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PlatformAuthorizationCatalogFilesTest {

    @Test
    void everyPlatformCatalogIsValid() throws Exception {
        Resource[] files = new PathMatchingResourcePatternResolver().getResources("classpath:db/authz/*.json");
        assertThat(files).extracting(Resource::getFilename).containsExactlyInAnyOrder("core.json", "ipms.json");
        ObjectMapper mapper = new ObjectMapper();
        for (Resource file : files) {
            try (InputStream is = file.getInputStream()) {
                AppAuthorizationCatalog catalog = mapper.readValue(is, AppAuthorizationCatalog.class);
                assertThat(catalog.validate()).as(file.getFilename()).isEmpty();
            }
        }
    }
}
