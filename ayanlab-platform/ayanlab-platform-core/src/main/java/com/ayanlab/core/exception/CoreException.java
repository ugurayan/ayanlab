package com.ayanlab.core.exception;

import java.util.Collections;
import java.util.Map;

/**
 * Generic runtime exception wrapper used across the core module.
 * Use this to wrap or throw any error instead of using raw runtime exceptions.
 */
public class CoreException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // Optional machine-friendly error code
    private final String code;

    // Optional metadata for extra context
    private final Map<String, Object> metadata;

    public CoreException(String message) {
        this(null, message, null, null);
    }

    public CoreException(String message, Throwable cause) {
        this(null, message, cause, null);
    }

    public CoreException(String code, String message) {
        this(code, message, null, null);
    }

    public CoreException(String code, String message, Throwable cause) {
        this(code, message, cause, null);
    }

    public CoreException(String code, String message, Map<String, Object> metadata) {
        this(code, message, null, metadata);
    }

    public CoreException(String code, String message, Throwable cause, Map<String, Object> metadata) {
        super(message, cause);
        this.code = code;
        this.metadata = metadata == null ? Collections.emptyMap() : Collections.unmodifiableMap(metadata);
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Convenient wrapper to convert any Throwable to a CoreException.
     * Maps common exception types to standard error codes:
     * - IllegalArgumentException -> INVALID_ARGUMENT
     * - IllegalStateException -> ILLEGAL_STATE
     * - NoSuchElementException -> NOT_FOUND
     * - SecurityException -> FORBIDDEN
     * - NullPointerException -> NULL_POINTER
     * For unknown types the code will be the simple class name of the cause.
     */
    public static CoreException wrap(Throwable t) {
        if (t == null) return new CoreException("UNKNOWN_ERROR", "Unknown error");
        if (t instanceof CoreException) return (CoreException) t;

        String code = t.getClass().getSimpleName();
        if (t instanceof IllegalArgumentException) code = "INVALID_ARGUMENT";
        else if (t instanceof IllegalStateException) code = "ILLEGAL_STATE";
        else if (t instanceof java.util.NoSuchElementException) code = "NOT_FOUND";
        else if (t instanceof SecurityException) code = "FORBIDDEN";
        else if (t instanceof NullPointerException) code = "NULL_POINTER";

        String msg = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
        return new CoreException(code, msg, t);
    }
}
