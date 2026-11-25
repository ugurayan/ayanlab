package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.LegalEntity;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.repository.LegalEntityRepository;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.service.LegalEntityService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.util.StaffingRules;
import com.ayanlab.hrm.backend.util.StaffingScope;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LegalEntityServiceImpl implements LegalEntityService {

    private static final String READ = "staffiq:legal_entities:read";
    private static final String CREATE = "staffiq:legal_entities:create";
    private static final String UPDATE = "staffiq:legal_entities:update";
    private static final String DELETE = "staffiq:legal_entities:delete";

    private final LegalEntityRepository repository;
    private final OrganizationRepository organizations;
    private final LocationRepository locations;
    private final HrmPlatformAuthorizationService platformAuthorization;

    @Override
    public List<LegalEntity> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, READ);
        if (context != null && context.getOrgId() != null) {
            return repository.findByPlatformOrganizationId(context.getOrgId());
        }
        return repository.findAll();
    }

    @Override
    public LegalEntity findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, READ);
        LegalEntity row = live(id);
        StaffingScope.assertSameOrg(context, row.getPlatformOrganizationId());
        return row;
    }

    @Override
    @Transactional
    public LegalEntity create(LegalEntity body, CoreContext context) {
        platformAuthorization.requirePermission(context, CREATE);
        body.setPlatformOrganizationId(StaffingScope.organizationId(context, body.getPlatformOrganizationId()));
        requireProfile(body.getPlatformOrganizationId());
        validate(body);
        body.setId(null);
        body.markNew(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public LegalEntity update(LegalEntity body, CoreContext context) {
        platformAuthorization.requirePermission(context, UPDATE);
        LegalEntity existing = live(body.getId());
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        body.setPlatformOrganizationId(existing.getPlatformOrganizationId());
        validate(body);
        body.setId(existing.getId());
        body.setCreatedAt(existing.getCreatedAt());
        body.setCreatedBy(existing.getCreatedBy());
        body.markUpdated(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, DELETE);
        LegalEntity existing = live(id);
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        if (locations.existsByReferenceEntityAndReferenceId(ReferenceEntityKind.LEGAL_ENTITY, existing.getId())) {
            throw StaffingRules.conflict("Legal entity still has locations");
        }
        existing.markDeleted(StaffingScope.actor(context));
        repository.save(existing);
    }

    private void requireProfile(UUID orgId) {
        if (organizations.findByPlatformOrganizationId(orgId).isEmpty()) {
            throw StaffingRules.bad("organization profile does not exist");
        }
    }

    private LegalEntity live(UUID id) {
        return repository.findById(id).filter(LegalEntity::live).orElseThrow(() -> StaffingRules.notFound("Legal entity not found"));
    }

    private static void validate(LegalEntity body) {
        StaffingRules.requireText(body.getLegalName(), "legalName");
        StaffingRules.requireText(body.getEntityType(), "entityType");
        StaffingRules.requireText(body.getVatNumber(), "vatNumber");
        StaffingRules.requireEmail(body.getEmail(), "email");
        StaffingRules.requireText(body.getPhone(), "phone");
    }
}
