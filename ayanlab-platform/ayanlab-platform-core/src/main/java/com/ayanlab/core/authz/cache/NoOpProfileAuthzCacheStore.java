package com.ayanlab.core.authz.cache;

import java.time.Duration;
import java.util.Optional;

public class NoOpProfileAuthzCacheStore implements ProfileAuthzCacheStore {

    @Override
    public Optional<ProfileAuthzSnapshot> get(String key) {
        return Optional.empty();
    }

    @Override
    public void put(String key, ProfileAuthzSnapshot snapshot, Duration ttl) {
    }
}
