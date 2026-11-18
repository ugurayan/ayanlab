package com.ayanlab.core.authz;

import com.ayanlab.core.entity.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bumps {@code App.authzVersion} / {@code UserProfile.authzVersion} after authorization data changes so that
 * access tokens minted from the old data are rejected (see {@code AuthzVersionChecker}).
 * Bumps are queued per transaction and applied after commit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthzVersionService {

    private static final String BUMP_APP = "UPDATE core.\"App\" SET \"authzVersion\" = COALESCE(\"authzVersion\", 0) + 1 WHERE id = ?";
    private static final String BUMP_APP_BY_CODE = "UPDATE core.\"App\" SET \"authzVersion\" = COALESCE(\"authzVersion\", 0) + 1 WHERE code = ?";
    private static final String BUMP_APP_BY_ROLE = "UPDATE core.\"App\" SET \"authzVersion\" = COALESCE(\"authzVersion\", 0) + 1 "
        + "WHERE id = (SELECT \"appId\" FROM core.\"Role\" WHERE id = ?)";
    private static final String BUMP_APP_BY_POLICY_GROUP = "UPDATE core.\"App\" SET \"authzVersion\" = COALESCE(\"authzVersion\", 0) + 1 "
        + "WHERE id = (SELECT \"appId\" FROM core.\"PolicyGroup\" WHERE id = ?)";
    private static final String BUMP_PROFILE = "UPDATE core.\"UserProfile\" SET \"authzVersion\" = COALESCE(\"authzVersion\", 0) + 1 WHERE id = ?";

    private static volatile AuthzVersionService instance;

    private final JdbcTemplate jdbc;

    @PostConstruct
    void register() {
        instance = this;
    }

    /** Entry point for {@link AuthzChangeListener}; no-op before the Spring context is up. */
    static void entityChanged(Object entity) {
        AuthzVersionService service = instance;
        if (service != null) {
            service.onEntityChanged(entity);
        }
    }

    void onEntityChanged(Object entity) {
        switch (entity) {
            case Role role -> enqueue(BUMP_APP, role.getAppId());
            case PolicyGroup group -> enqueue(BUMP_APP, group.getAppId());
            case Permission permission -> enqueue(BUMP_APP_BY_CODE, permission.getAppCode());
            case RolePolicyGroup link -> enqueue(BUMP_APP_BY_ROLE, link.getRoleId());
            case PolicyGroupPermission link -> enqueue(BUMP_APP_BY_POLICY_GROUP, link.getPolicyGroupId());
            case ProfileRole link -> enqueue(BUMP_PROFILE, link.getProfileId());
            case ProfilePolicyGroup link -> enqueue(BUMP_PROFILE, link.getProfileId());
            case ProfilePermission link -> enqueue(BUMP_PROFILE, link.getProfileId());
            case ProfileResourceScope link -> enqueue(BUMP_PROFILE, link.getProfileId());
            default -> { }
        }
    }

    public void bumpApp(UUID appId) {
        enqueue(BUMP_APP, appId);
    }

    public void bumpProfile(UUID profileId) {
        enqueue(BUMP_PROFILE, profileId);
    }

    private void enqueue(String sql, Object key) {
        if (key == null) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            jdbc.update(sql, key);
            return;
        }
        PendingBumps pending = (PendingBumps) TransactionSynchronizationManager.getResource(this);
        if (pending == null) {
            PendingBumps created = new PendingBumps();
            TransactionSynchronizationManager.bindResource(this, created);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    TransactionSynchronizationManager.unbindResourceIfPossible(AuthzVersionService.this);
                    if (status == STATUS_COMMITTED) {
                        created.flush(jdbc);
                    }
                }
            });
            pending = created;
        }
        pending.add(sql, key);
    }

    private static final class PendingBumps {
        private final Map<String, Object> bumps = new LinkedHashMap<>();

        void add(String sql, Object key) {
            bumps.putIfAbsent(sql + "|" + key, new Object[]{sql, key});
        }

        void flush(JdbcTemplate jdbc) {
            for (Object value : bumps.values()) {
                Object[] bump = (Object[]) value;
                try {
                    jdbc.update((String) bump[0], bump[1]);
                } catch (Exception e) {
                    log.warn("[AuthzVersionService] Version bump failed for {}: {}", bump[1], e.getMessage());
                }
            }
        }
    }
}
