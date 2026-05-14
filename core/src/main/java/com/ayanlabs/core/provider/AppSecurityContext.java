package com.ayanlabs.core.provider;

import lombok.*;
import java.util.*;

public class AppSecurityContext {

    private static final ThreadLocal<AppContext> CONTEXT = new ThreadLocal<>();

    @Data @Builder
    public static class AppContext {
        private UUID userId;
        private UUID profileId;
        private String appKey;
        private UUID orgId;
        private Set<String> roles;
        private Set<String> permissions;

        public boolean hasRole(String role) { return roles != null && roles.contains(role); }
        public boolean hasPermission(String perm) { return permissions != null && permissions.contains(perm); }
        public boolean hasAnyRole(String... r) { return Arrays.stream(r).anyMatch(this::hasRole); }
    }

    public static void set(AppContext ctx) { CONTEXT.set(ctx); }
    public static AppContext get() { return CONTEXT.get(); }
    public static void clear() { CONTEXT.remove(); }
    public static UUID currentUserId() { return get() != null ? get().getUserId() : null; }
    public static String currentAppKey() { return get() != null ? get().getAppKey() : null; }
    public static UUID currentProfileId() { return get() != null ? get().getProfileId() : null; }
}
