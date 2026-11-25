package com.ayanlab.hrm.backend.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Parses a UUID, or a stable UUID derived from a legacy slug such as {@code org_acme}. */
public final class Ids {

    private Ids() {}

    public static UUID parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ex) {
            return UUID.nameUUIDFromBytes(raw.trim().getBytes(StandardCharsets.UTF_8));
        }
    }
}
