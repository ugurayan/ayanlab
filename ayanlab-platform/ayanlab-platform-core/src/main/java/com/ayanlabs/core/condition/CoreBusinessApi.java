package com.ayanlabs.core.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Profile-scoped platform business REST — exposed when {@code ayanlabs.expose.core-business-apis=true}
 * (core backend). Paths use {@link com.ayanlabs.core.web.ApiPathPrefixes#CORE}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "ayanlabs.expose.core-business-apis", havingValue = "true", matchIfMissing = true)
public @interface CoreBusinessApi {
}