package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.TextMessageAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface TextMessageAccountRepository extends IpmsOrgRepository<TextMessageAccount, java.util.UUID> {
}
