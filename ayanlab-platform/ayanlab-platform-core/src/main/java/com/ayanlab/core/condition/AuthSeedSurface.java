package com.ayanlab.core.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auth/reference seed loaders — run when {@code platform.db.seed.surface=platform} (unified core backend).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "platform.db.seed.surface", havingValue = "platform", matchIfMissing = true)
public @interface AuthSeedSurface {
}