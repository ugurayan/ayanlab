package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.communication.Announcement;
import com.ayanlabs.propertymgmt.repository.AnnouncementRepository;
import com.ayanlabs.propertymgmt.service.IAnnouncementService;
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
