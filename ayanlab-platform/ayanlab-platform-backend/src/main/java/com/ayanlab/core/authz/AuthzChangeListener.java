package com.ayanlab.core.authz;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

/** JPA listener on authorization entities; see {@link AuthzVersionService}. */
public class AuthzChangeListener {

    @PostPersist
    @PostUpdate
    @PostRemove
    void changed(Object entity) {
        AuthzVersionService.entityChanged(entity);
    }
}
