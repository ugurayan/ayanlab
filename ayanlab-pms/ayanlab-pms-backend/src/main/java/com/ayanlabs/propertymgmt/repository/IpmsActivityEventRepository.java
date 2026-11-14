package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.platform.IpmsActivityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IpmsActivityEventRepository extends JpaRepository<IpmsActivityEvent, UUID> {
    List<IpmsActivityEvent> findByAppIdAndIsActiveTrueOrderByOccurredAtDesc(UUID appId);

    List<IpmsActivityEvent> findByAppIdAndOrganizationIdAndIsActiveTrueOrderByOccurredAtDesc(
            UUID appId, UUID organizationId);

    Optional<IpmsActivityEvent> findByIdAndAppId(UUID id, UUID appId);
}
