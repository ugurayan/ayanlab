package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends IpmsOrgRepository<Message, java.util.UUID> {
}
