package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.SubscriptionAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionAccountRepository extends IpmsOrgRepository<SubscriptionAccount, java.util.UUID> {
}
