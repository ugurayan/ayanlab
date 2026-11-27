package com.ayanlab.core.authz.cache;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import com.ayanlab.core.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileAuthzCacheService {

    private static final Logger log = LoggerFactory.getLogger(ProfileAuthzCacheService.class);

    private final AuthzCacheProperties properties;
    private final ProfileAuthzCacheStore store;
    private final ProfileAuthzSource source;
    private final Clock clock;

    @Autowired
    public ProfileAuthzCacheService(
            AuthzCacheProperties properties, ProfileAuthzCacheStore store, ProfileAuthzSource source) {
        this(properties, store, source, Clock.systemUTC());
    }

    ProfileAuthzCacheService(
            AuthzCacheProperties properties,
            ProfileAuthzCacheStore store,
            ProfileAuthzSource source,
            Clock clock) {
        this.properties = properties;
        this.store = store;
        this.source = source;
        this.clock = clock;
    }

    public ProfileAuthzSnapshot resolve(CoreContext context, String tokenAuthzVersion) {
        if (context == null || context.getProfileId() == null) {
            throw new ForbiddenException("Profile context required");
        }
        UUID profileId = context.getProfileId();
        String key = ProfileAuthzCacheKeys.key(properties.getKeyPrefix(), profileId);
        if (properties.isEnabled()) {
            Optional<ProfileAuthzSnapshot> cached = read(key);
            if (cached.isPresent() && ProfileAuthzSnapshots.reusable(cached.get(), tokenAuthzVersion)) {
                return cached.get();
            }
        }
        EffectiveAuthorization fresh = source.load(context);
        Duration ttl = ttl();
        ProfileAuthzSnapshot snapshot = ProfileAuthzSnapshots.from(fresh, clock.instant().plus(ttl));
        write(key, snapshot, ttl);
        return snapshot;
    }

    /** Stores a snapshot the caller already resolved, such as platform login. */
    public void remember(EffectiveAuthorization authorization) {
        if (!properties.isEnabled() || authorization == null || authorization.getProfileId() == null) {
            return;
        }
        Duration ttl = ttl();
        Instant expiresAt = clock.instant().plus(ttl);
        write(
                ProfileAuthzCacheKeys.key(properties.getKeyPrefix(), authorization.getProfileId()),
                ProfileAuthzSnapshots.from(authorization, expiresAt),
                ttl);
    }

    private Optional<ProfileAuthzSnapshot> read(String key) {
        try {
            return store.get(key);
        } catch (RuntimeException ex) {
            log.warn("authz cache read failed for {}: {}", key, ex.toString());
            return Optional.empty();
        }
    }

    private void write(String key, ProfileAuthzSnapshot snapshot, Duration ttl) {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            store.put(key, snapshot, ttl);
        } catch (RuntimeException ex) {
            log.warn("authz cache write failed for {}: {}", key, ex.toString());
        }
    }

    private Duration ttl() {
        Duration ttl = properties.getTtl();
        return ttl == null || ttl.isZero() || ttl.isNegative() ? Duration.ofHours(1) : ttl;
    }
}
