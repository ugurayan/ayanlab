package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.accounting.Budget;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends IpmsOrgRepository<Budget, java.util.UUID> {
}
