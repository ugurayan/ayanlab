package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.AppNotification;
import org.springframework.stereotype.Repository;

@Repository
public interface AppNotificationRepository extends IpmsOrgRepository<AppNotification, java.util.UUID> {
}
