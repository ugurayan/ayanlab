package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.associations.CoopBoardPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface CoopBoardPacketRepository extends IpmsOrgRepository<CoopBoardPacket, java.util.UUID> {
}
