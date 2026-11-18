package com.ayanlab.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds standard AyanLabs client headers into {@link com.ayanlab.core.data.ClientHeadersPayload}.
 * Use {@code requireTokens = false} on sign-in / public catalog endpoints.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientHeaders {

    boolean requireTokens() default true;

    /** When false, X-App-Id is optional (public catalog, pre-login). */
    boolean requireAppId() default true;
}
