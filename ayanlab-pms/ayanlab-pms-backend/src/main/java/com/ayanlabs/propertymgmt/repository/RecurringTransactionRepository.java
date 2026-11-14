package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.accounting.RecurringTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringTransactionRepository extends IpmsOrgRepository<RecurringTransaction, java.util.UUID> {
}
