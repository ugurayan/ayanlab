package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.associations.OwnershipAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnershipAccountRepository extends IpmsOrgRepository<OwnershipAccount, java.util.UUID> {
    java.util.List<OwnershipAccount> findByOrganizationIdAndAssociationIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID associationId);
}
