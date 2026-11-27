package com.ayanlab.core.authz.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

public class RedisProfileAuthzCacheStore implements ProfileAuthzCacheStore {

    private static final Logger log = LoggerFactory.getLogger(RedisProfileAuthzCacheStore.class);

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public RedisProfileAuthzCacheStore(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ProfileAuthzSnapshot> get(String key) {
        try {
            String json = redis.opsForValue().get(key);
            if (json == null || json.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, ProfileAuthzSnapshot.class));
        } catch (Exception ex) {
            log.warn("authz cache read failed for {}: {}", key, ex.toString());
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, ProfileAuthzSnapshot snapshot, Duration ttl) {
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(snapshot), ttl);
        } catch (Exception ex) {
            log.warn("authz cache write failed for {}: {}", key, ex.toString());
        }
    }
}
