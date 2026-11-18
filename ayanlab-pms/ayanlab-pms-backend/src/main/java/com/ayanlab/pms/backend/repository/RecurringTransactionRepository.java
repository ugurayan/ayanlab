package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.RecurringTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringTransactionRepository extends IpmsOrgRepository<RecurringTransaction, java.util.UUID> {
}
