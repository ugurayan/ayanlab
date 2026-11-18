package com.ayanlab.core.util;

import com.ayanlab.core.exception.CoreException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern SAFE_IDENTIFIER_PATTERN =  Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    private static final Pattern BIRTHDATE_PATTERN=  Pattern.compile("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/(19|20)\\d{2}$");

    private static final DateTimeFormatter BIRTHDATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new CoreException("VALIDATION_ERROR",fieldName + " is required.");
        }
    }


    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " is required and cannot be blank.");
        }
    }

    public static void requireMinLength(String value, String fieldName, int minLength) {
        requireNonBlank(value, fieldName);

        if (value.length() < minLength) {
            throw new CoreException("VALIDATION_ERROR",
                    fieldName + " must be at least " + minLength + " characters long."
            );
        }
    }

    public static void requireMaxLength(String value, String fieldName, int maxLength) {
        requireNonBlank(value, fieldName);

        if (value.length() > maxLength) {
            throw new CoreException("VALIDATION_ERROR",
                    fieldName + " must be at most " + maxLength + " characters long."
            );
        }
    }

    public static void requireLengthBetween(
            String value,
            String fieldName,
            int minLength,
            int maxLength
    ) {
        requireNonBlank(value, fieldName);

        if (value.length() < minLength || value.length() > maxLength) {
            throw new CoreException("VALIDATION_ERROR",
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters."
            );
        }
    }

    public static void requireValidEmail(String value, String fieldName) {
        requireNonBlank(value, fieldName);

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " must be a valid email address.");
        }
    }

    public static void requireValidBirthDate(String value, String fieldName) {
        requireNonBlank(value, fieldName);

        if (!BIRTHDATE_PATTERN.matcher(value).matches()) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " must match MM/DD/YYYY format.");
        }

        try {
            LocalDate.parse(value, BIRTHDATE_FORMATTER);
        } catch (DateTimeException ex) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " must be a valid calendar date in MM/DD/YYYY format.");
        }
    }

    public static void requireValidUuid(UUID value, String fieldName) {
        if (value == null) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " must be a valid UUID.");
        }
    }

    public static void requirePositive(Number value, String fieldName) {
        requireNonNull(value, fieldName);

        if (value.doubleValue() <= 0) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " must be greater than 0.");
        }
    }

    public static void requireZeroOrPositive(Number value, String fieldName) {
        requireNonNull(value, fieldName);

        if (value.doubleValue() < 0) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " cannot be negative.");
        }
    }

    public static void requireTrue(boolean condition, String errorMessage) {
        if (!condition) {
            throw new CoreException("VALIDATION_ERROR",errorMessage);
        }
    }

    public static void requireFalse(boolean condition, String errorMessage) {
        if (condition) {
            throw new CoreException("VALIDATION_ERROR", errorMessage);
        }
    }

    public static void requireNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " is required and cannot be empty.");
        }
    }

    public static void requireNotEmpty(Map<?, ?> map, String fieldName) {
        if (map == null || map.isEmpty()) {
            throw new CoreException("VALIDATION_ERROR", fieldName + " is required and cannot be empty.");
        }
    }

    public static void requireSafeIdentifier(String value, String fieldName) {
        requireNonBlank(value, fieldName);

        if (!SAFE_IDENTIFIER_PATTERN.matcher(value).matches()) {
            throw new CoreException("VALIDATION_ERROR",
                    fieldName + " can only contain letters, numbers, and underscores, and must start with a letter or underscore."
            );
        }
    }

    public static void requireEnumValue(String value, Class<? extends Enum<?>> enumClass, String fieldName) {
        requireNonBlank(value, fieldName);

        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(value)) {
                return;
            }
        }

        throw new CoreException("VALIDATION_ERROR", fieldName + " has an invalid value: " + value);
    }
}
