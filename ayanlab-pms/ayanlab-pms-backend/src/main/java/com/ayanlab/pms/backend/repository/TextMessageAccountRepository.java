package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.TextMessageAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface TextMessageAccountRepository extends IpmsOrgRepository<TextMessageAccount, java.util.UUID> {
}
