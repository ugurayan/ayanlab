package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.DeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, UUID> {
    List<DeliveryLog> findByCampaignId(UUID campaignId);
    List<DeliveryLog> findByRecipientEmail(String recipientEmail);
    List<DeliveryLog> findByOutboxId(UUID outboxId);
}
