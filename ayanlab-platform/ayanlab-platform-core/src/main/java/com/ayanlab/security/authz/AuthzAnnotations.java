package com.ayanlab.security.authz;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

final class AuthzAnnotations {

    private AuthzAnnotations() {}

    /** Method-level annotation if present, otherwise the class-level one. */
    static <A extends Annotation> A effective(JoinPoint jp, Class<A> type) {
        Class<?> targetClass = jp.getTarget() != null ? jp.getTarget().getClass() : jp.getSignature().getDeclaringType();
        Method method = AopUtils.getMostSpecificMethod(((MethodSignature) jp.getSignature()).getMethod(), targetClass);
        A onMethod = AnnotatedElementUtils.findMergedAnnotation(method, type);
        return onMethod != null ? onMethod : AnnotatedElementUtils.findMergedAnnotation(targetClass, type);
    }
}
