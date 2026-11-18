package com.ayanlab.core.authz;

import com.ayanlab.core.entity.ProfileRole;
import com.ayanlab.core.entity.Role;
import com.ayanlab.core.entity.RolePolicyGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthzVersionServiceTest {

    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final AuthzVersionService service = new AuthzVersionService(jdbc);

    @AfterEach
    void cleanup() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.unbindResourceIfPossible(service);
    }

    @Test
    void bumpsAfterCommitOnlyOncePerKey() {
        UUID appId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TransactionSynchronizationManager.initSynchronization();

        service.onEntityChanged(Role.builder().appId(appId).code("A").build());
        service.onEntityChanged(Role.builder().appId(appId).code("B").build());
        service.onEntityChanged(ProfileRole.builder().profileId(profileId).build());
        verifyNoInteractions(jdbc);

        complete(TransactionSynchronization.STATUS_COMMITTED);

        verify(jdbc, times(1)).update(contains("\"App\""), eq(appId));
        verify(jdbc, times(1)).update(contains("\"UserProfile\""), eq(profileId));
    }

    @Test
    void rollbackDiscardsBumps() {
        TransactionSynchronizationManager.initSynchronization();
        service.onEntityChanged(RolePolicyGroup.builder().roleId(UUID.randomUUID()).build());

        complete(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(jdbc, never()).update(anyString(), any(Object[].class));
        verify(jdbc, never()).update(anyString(), any(Object.class));
    }

    @Test
    void bumpsImmediatelyWithoutTransaction() {
        UUID profileId = UUID.randomUUID();
        service.onEntityChanged(ProfileRole.builder().profileId(profileId).build());
        verify(jdbc).update(contains("\"UserProfile\""), eq(profileId));
    }

    private static void complete(int status) {
        var synchronizations = TransactionSynchronizationManager.getSynchronizations();
        TransactionSynchronizationManager.clearSynchronization();
        synchronizations.forEach(s -> s.afterCompletion(status));
    }
}
