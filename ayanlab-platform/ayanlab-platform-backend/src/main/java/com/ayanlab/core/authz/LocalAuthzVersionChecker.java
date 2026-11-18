package com.ayanlab.core.authz;

import com.ayanlab.security.authz.AuthzVersionChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Platform-side checker: reads the versions straight from the {@code core} schema (briefly cached). */
@Component
public class LocalAuthzVersionChecker implements AuthzVersionChecker {

    private static final String QUERY = "SELECT a.\"authzVersion\", p.\"authzVersion\" FROM core.\"App\" a, core.\"UserProfile\" p "
        + "WHERE a.code = ? AND p.id = ?";

    private record Cached(String version, Instant expiresAt) {}

    private final JdbcTemplate jdbc;
    private final Map<String, Cached> cache = new ConcurrentHashMap<>();

    @Value("${platform.authz.version-check.cache-seconds:5}")
    private long cacheSeconds;

    public LocalAuthzVersionChecker(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String currentVersion(String appCode, UUID profileId) {
        String key = appCode + "|" + profileId;
        Instant now = Instant.now();
        Cached cached = cache.get(key);
        if (cached != null && cached.expiresAt().isAfter(now)) {
            return cached.version();
        }
        List<String> rows = jdbc.query(QUERY,
            (rs, i) -> AuthzVersionChecker.format((Long) rs.getObject(1), (Long) rs.getObject(2)), appCode, profileId);
        String version = rows.isEmpty() ? null : rows.get(0);
        if (version != null) {
            cache.put(key, new Cached(version, now.plusSeconds(cacheSeconds)));
        }
        return version;
    }
}
