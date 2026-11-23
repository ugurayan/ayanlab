package com.ayanlab.hrm.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * StaffIQ app configuration.
 * <p>
 * Login, JWT issuance, and authorization catalog live on {@code ayanlab-platform-backend}
 * ({@code platform.api-url}). This service validates tokens via {@code com.ayanlab.security}
 * and exposes domain REST under {@code /hrm/api/**} only.
 * {@code authz/*.json} is published to the platform on startup
 * ({@code platform.authz.catalog.publish.enabled=true}).
 */
@Configuration
@EnableConfigurationProperties(HrmDataProperties.class)
public class HrmBackendConfig {}