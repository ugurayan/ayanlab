package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.associations.CoopBoardPacket;
import com.ayanlab.pms.backend.repository.CoopBoardPacketRepository;
import com.ayanlab.pms.backend.service.ICoopBoardPacketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CoopBoardPacketService extends AbstractIpmsOrgService<CoopBoardPacket, CoopBoardPacketRepository> implements ICoopBoardPacketService {

    public CoopBoardPacketService(CoopBoardPacketRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "CoopBoardPacket";
    }
}
