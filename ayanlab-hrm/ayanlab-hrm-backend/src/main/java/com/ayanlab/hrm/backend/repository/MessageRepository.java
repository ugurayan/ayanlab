package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByProfileId(String profileId);
    List<Message> findByOrgId(String orgId);
    List<Message> findByWorkerId(String workerId);
}
