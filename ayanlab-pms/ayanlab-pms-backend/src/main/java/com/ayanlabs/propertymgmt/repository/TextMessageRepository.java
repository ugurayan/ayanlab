package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.TextMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface TextMessageRepository extends IpmsOrgRepository<TextMessage, java.util.UUID> {
}
