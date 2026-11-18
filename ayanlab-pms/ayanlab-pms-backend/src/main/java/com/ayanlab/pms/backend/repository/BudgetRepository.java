package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.Budget;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends IpmsOrgRepository<Budget, java.util.UUID> {
}
