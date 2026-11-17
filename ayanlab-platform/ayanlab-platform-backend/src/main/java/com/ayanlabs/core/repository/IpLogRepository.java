package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.IpLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IpLogRepository extends JpaRepository<IpLog, UUID> {
    List<IpLog> findByIpAddressOrderByLoggedAtDesc(String ipAddress);

    List<IpLog> findByUserIdOrderByLoggedAtDesc(UUID userId);
}