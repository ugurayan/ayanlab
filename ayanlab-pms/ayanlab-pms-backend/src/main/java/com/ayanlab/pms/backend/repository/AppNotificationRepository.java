package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.AppNotification;
import org.springframework.stereotype.Repository;

@Repository
public interface AppNotificationRepository extends IpmsOrgRepository<AppNotification, java.util.UUID> {
}
