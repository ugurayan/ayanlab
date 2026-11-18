package com.ayanlab.core.repository;

import com.ayanlab.core.entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankCardRepository extends JpaRepository<BankCard, UUID> {
    List<BankCard> findByProfileIdAndIsActiveTrueOrderByCreatedAtDesc(UUID profileId);

    List<BankCard> findByProfileIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(UUID profileId, UUID appId);
}