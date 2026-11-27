package com.ayanlab.core.authz.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(AuthzCacheProperties.class)
public class AuthzCacheConfiguration {

    @Bean(destroyMethod = "destroy")
    @ConditionalOnProperty(prefix = "ayanlabs.authz-cache", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LettuceConnectionFactory authzLettuceConnectionFactory(AuthzCacheProperties properties) {
        AuthzCacheProperties.Redis redis = properties.getRedis();
        RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration(redis.getHost(), redis.getPort());
        standalone.setDatabase(redis.getDatabase());
        if (StringUtils.hasText(redis.getPassword())) {
            standalone.setPassword(RedisPassword.of(redis.getPassword()));
        }
        LettuceConnectionFactory factory = new LettuceConnectionFactory(standalone);
        factory.setValidateConnection(false);
        return factory;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ayanlabs.authz-cache", name = "enabled", havingValue = "true", matchIfMissing = true)
    public StringRedisTemplate authzStringRedisTemplate(LettuceConnectionFactory authzLettuceConnectionFactory) {
        return new StringRedisTemplate(authzLettuceConnectionFactory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ayanlabs.authz-cache", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ProfileAuthzCacheStore redisProfileAuthzCacheStore(
            StringRedisTemplate authzStringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisProfileAuthzCacheStore(authzStringRedisTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ayanlabs.authz-cache", name = "enabled", havingValue = "false")
    public ProfileAuthzCacheStore noOpProfileAuthzCacheStore() {
        return new NoOpProfileAuthzCacheStore();
    }
}
