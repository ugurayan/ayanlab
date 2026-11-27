package com.ayanlab.core.repository;

import com.ayanlab.core.entity.BankRoutingInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankRoutingInstitutionRepository extends JpaRepository<BankRoutingInstitution, UUID> {

    Optional<BankRoutingInstitution> findByRoutingNumber(String routingNumber);

    List<BankRoutingInstitution> findTop25ByRoutingNumberStartingWithAndIsActiveTrueOrderByRoutingNumberAsc(
        String routingPrefix
    );

    List<BankRoutingInstitution> findTop25ByInstitutionNameContainingIgnoreCaseAndIsActiveTrueOrderByInstitutionNameAsc(
        String namePart
    );
}