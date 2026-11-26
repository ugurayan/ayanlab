package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.EmailOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, UUID> {
    List<EmailOutbox> findByStatus(String status);
    List<EmailOutbox> findByCampaignId(UUID campaignId);
}
