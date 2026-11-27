package com.ayanlab.core.authz.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "ayanlabs.authz-cache")
public class AuthzCacheProperties {

    private boolean enabled = true;

    /** Redis and sessionStorage key prefix, for example {@code hrm} or {@code crm}. */
    private String keyPrefix = "platform";

    private Duration ttl = Duration.ofHours(1);

    /** Product API root that exposes {@code /authorization/snapshot}. */
    private String apiBase = "/auth/api";

    /** {@code remote} calls the platform; {@code local} resolves inside the platform JVM. */
    private String source = "remote";

    private String platformApiUrl = "http://localhost:8102";

    private Redis redis = new Redis();

    @Data
    public static class Redis {
        private String host = "localhost";
        private int port = 6379;
        private String password = "";
        private int database = 0;
    }
}
