package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.TextMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface TextMessageRepository extends IpmsOrgRepository<TextMessage, java.util.UUID> {
}
