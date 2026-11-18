package com.ayanlab.core.repository;

import com.ayanlab.core.entity.CloudFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CloudFileRepository extends JpaRepository<CloudFile, UUID> {

    List<CloudFile> findByAppIdOrderByUpdatedAtDesc(UUID appId);
}

