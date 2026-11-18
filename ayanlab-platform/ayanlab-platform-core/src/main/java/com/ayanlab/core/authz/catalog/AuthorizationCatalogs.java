package com.ayanlab.core.authz.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public final class AuthorizationCatalogs {

    public static final String DEFAULT_LOCATION = "classpath:authz/*.json";

    private AuthorizationCatalogs() {}

    /** Reads every file matching {@code locationPattern} (sorted by name) and merges them into one catalog. */
    public static AppAuthorizationCatalog load(String locationPattern, ObjectMapper objectMapper) throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(locationPattern);
        if (resources.length == 0) {
            throw new IOException("No catalog files match " + locationPattern);
        }
        Arrays.sort(resources, Comparator.comparing(r -> Objects.requireNonNullElse(r.getFilename(), "")));
        AppAuthorizationCatalog merged = null;
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                AppAuthorizationCatalog part = objectMapper.readValue(is, AppAuthorizationCatalog.class);
                merged = merged == null ? part : merged.merge(part);
            }
        }
        return merged;
    }
}
