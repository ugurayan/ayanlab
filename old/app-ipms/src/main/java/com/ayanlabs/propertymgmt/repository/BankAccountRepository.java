package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.accounting.BankAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends IpmsOrgRepository<BankAccount, java.util.UUID> {
}
