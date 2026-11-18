package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.IpmsTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface IpmsTransactionRepository extends IpmsOrgRepository<IpmsTransaction, java.util.UUID> {
    java.util.List<IpmsTransaction> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);
}
