package com.ayanlab.core.authz.cache;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileAuthzCacheServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-26T20:00:00Z");
    private static final Duration TTL = Duration.ofHours(1);
    private final UUID profileId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void keyIsPrefixColonProfileId() {
        assertThat(ProfileAuthzCacheKeys.key("hrm", profileId))
                .isEqualTo("hrm:11111111-1111-1111-1111-111111111111");
    }

    @Test
    void freshCacheHitSkipsPlatform() {
        MemoryStore store = new MemoryStore();
        store.saved = snapshot("2.4", NOW.plus(TTL));
        AtomicInteger loads = new AtomicInteger();
        ProfileAuthzCacheService service = service(store, loads);

        ProfileAuthzSnapshot result = service.resolve(context(), "2.4");

        assertThat(result.permissions()).containsExactly("staffiq:shifts:read");
        assertThat(result.roles()).containsExactly("ORG_ADMIN");
        assertThat(result.policyGroups()).containsExactly("employer");
        assertThat(loads).hasValue(0);
    }

    @Test
    void versionMismatchReloadsAndRewrites() {
        MemoryStore store = new MemoryStore();
        store.saved = snapshot("2.4", NOW.plus(TTL));
        AtomicInteger loads = new AtomicInteger();
        ProfileAuthzCacheService service = service(store, loads);

        ProfileAuthzSnapshot result = service.resolve(context(), "3.0");

        assertThat(loads).hasValue(1);
        assertThat(result.authzVersion()).isEqualTo("9.1");
        assertThat(result.permissions()).containsExactly("staffiq:shifts:create");
        assertThat(store.saved.authzVersion()).isEqualTo("9.1");
        assertThat(store.ttl).isEqualTo(TTL);
        assertThat(store.saved.expiresAt()).isEqualTo(NOW.plus(TTL));
    }

    @Test
    void redisReadFailureFallsBackToPlatform() {
        ProfileAuthzCacheStore store = new ProfileAuthzCacheStore() {
            @Override
            public Optional<ProfileAuthzSnapshot> get(String key) {
                throw new IllegalStateException("redis down");
            }

            @Override
            public void put(String key, ProfileAuthzSnapshot snapshot, Duration ttl) {
                throw new IllegalStateException("redis down");
            }
        };
        AtomicInteger loads = new AtomicInteger();
        ProfileAuthzSnapshot result = service(store, loads).resolve(context(), "9.1");

        assertThat(loads).hasValue(1);
        assertThat(result.permissions()).containsExactly("staffiq:shifts:create");
    }

    private ProfileAuthzCacheService service(ProfileAuthzCacheStore store, AtomicInteger loads) {
        AuthzCacheProperties properties = new AuthzCacheProperties();
        properties.setEnabled(true);
        properties.setKeyPrefix("hrm");
        properties.setTtl(TTL);
        ProfileAuthzSource source = context -> {
            loads.incrementAndGet();
            return EffectiveAuthorization.builder()
                    .profileId(profileId)
                    .authzVersion("9.1")
                    .permissionCodes(new LinkedHashSet<>(List.of("staffiq:shifts:create")))
                    .roleCodes(new LinkedHashSet<>(List.of("WORKER")))
                    .policyGroupCodes(new LinkedHashSet<>(List.of("worker")))
                    .build();
        };
        return new ProfileAuthzCacheService(properties, store, source, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private CoreContext context() {
        return CoreContext.builder().profileId(profileId).token("jwt").build();
    }

    private ProfileAuthzSnapshot snapshot(String version, Instant expiresAt) {
        return new ProfileAuthzSnapshot(
                profileId,
                List.of("staffiq:shifts:read"),
                List.of("ORG_ADMIN"),
                List.of("employer"),
                version,
                expiresAt);
    }

    private static final class MemoryStore implements ProfileAuthzCacheStore {
        private ProfileAuthzSnapshot saved;
        private Duration ttl;

        @Override
        public Optional<ProfileAuthzSnapshot> get(String key) {
            return Optional.ofNullable(saved);
        }

        @Override
        public void put(String key, ProfileAuthzSnapshot snapshot, Duration ttl) {
            this.saved = snapshot;
            this.ttl = ttl;
        }
    }
}
