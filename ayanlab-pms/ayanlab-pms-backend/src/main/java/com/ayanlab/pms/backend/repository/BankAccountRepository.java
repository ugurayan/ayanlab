package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.BankAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends IpmsOrgRepository<BankAccount, java.util.UUID> {
}
