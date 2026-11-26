package com.ayanlab.comm.backend.util;

import com.ayanlab.comm.backend.entity.BaseEntity;
import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {}

    public static UUID newId() {
        return UUID.randomUUID();
    }

    public static void assignIfAbsent(BaseEntity entity) {
        if (entity != null && entity.getId() == null) {
            entity.setId(newId());
            entity.markNew();
        }
    }
}
