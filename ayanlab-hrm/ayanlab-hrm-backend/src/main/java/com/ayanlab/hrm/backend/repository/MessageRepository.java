package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByProfileId(UUID profileId);
    List<Message> findByOrgId(UUID orgId);
    List<Message> findByWorkerId(UUID workerId);
}
