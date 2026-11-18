package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.SubscriptionAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionAccountRepository extends IpmsOrgRepository<SubscriptionAccount, java.util.UUID> {
}
