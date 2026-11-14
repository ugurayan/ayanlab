package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends IpmsOrgRepository<Message, java.util.UUID> {
}
