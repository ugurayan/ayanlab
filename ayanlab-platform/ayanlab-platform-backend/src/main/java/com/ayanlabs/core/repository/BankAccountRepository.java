package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByProfileIdAndIsActiveTrueOrderByCreatedAtDesc(UUID profileId);

    List<BankAccount> findByProfileIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(UUID profileId, UUID appId);
}