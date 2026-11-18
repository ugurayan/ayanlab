package com.ayanlab.core.util;

import com.ayanlab.core.exception.CoreException;

import java.security.SecureRandom;

public final class GeneratorUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String NUMERIC = "0123456789";
    private static final String ALPHA_NUMERIC =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private GeneratorUtils() {
        throw new CoreException("INTERNAL_ERROR", "Utility class cannot be instantiated");
    }

    public static String generate(int length, boolean numericOnly, boolean alphaNumeric) {
        if (length <= 0) {
            throw new CoreException("VALIDATION_ERROR", "generate method input length must be greater than 0.");
        }

        if (numericOnly && alphaNumeric) {
            throw new CoreException("VALIDATION_ERROR",  "numericOnly and alphaNumeric cannot both be true.");
        }

        String characters;

        if (numericOnly) {
            characters = NUMERIC;
        } else if (alphaNumeric) {
            characters = ALPHA_NUMERIC;
        } else {
            characters = ALPHA_NUMERIC;
        }

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
}
