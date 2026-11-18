package com.ayanlab.core.repository;

import com.ayanlab.core.entity.OrganizationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationSubscriptionRepository extends JpaRepository<OrganizationSubscription, UUID> {
    List<OrganizationSubscription> findByOrganizationIdOrderByStartedAtDesc(UUID organizationId);
}
