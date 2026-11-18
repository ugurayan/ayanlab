package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlab.pms.backend.entity.communication.Announcement;
import com.ayanlab.pms.backend.repository.AnnouncementRepository;
import com.ayanlab.pms.backend.service.IAnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnnouncementService extends AbstractIpmsOrgService<Announcement, AnnouncementRepository> implements IAnnouncementService {

    public AnnouncementService(AnnouncementRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> findAll(CoreContext context, UUID associationId) {
        UUID orgId = requireOrgId(context);
        if (associationId != null) {
            return repository.findByOrganizationIdAndAssociationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, associationId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "Announcement";
    }
}
