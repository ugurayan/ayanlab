package com.ayanlab.core.authz.cache;

import java.time.Duration;
import java.util.Optional;

public interface ProfileAuthzCacheStore {

    Optional<ProfileAuthzSnapshot> get(String key);

    void put(String key, ProfileAuthzSnapshot snapshot, Duration ttl);
}
