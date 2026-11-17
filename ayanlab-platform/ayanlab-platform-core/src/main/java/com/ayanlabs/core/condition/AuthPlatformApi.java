package com.ayanlabs.core.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RBAC / identity platform REST — exposed when {@code ayanlabs.expose.auth-platform-apis=true}
 * (auth backend). Paths use {@link com.ayanlabs.core.web.ApiPathPrefixes#AUTH}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "ayanlabs.expose.auth-platform-apis", havingValue = "true")
public @interface AuthPlatformApi {
}