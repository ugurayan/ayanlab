package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.repository.BrandRepository;
import com.ayanlab.hrm.backend.repository.LegalEntityRepository;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.repository.ShiftTemplateRepository;
import com.ayanlab.hrm.backend.service.OrganizationService;
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
public class OrganizationServiceImpl implements OrganizationService {

    private static final String PERM_READ = "staffiq:organizations:read";
    private static final String PERM_CREATE = "staffiq:organizations:create";
    private static final String PERM_UPDATE = "staffiq:organizations:update";
    private static final String PERM_DELETE = "staffiq:organizations:delete";

    private final OrganizationRepository repository;
    private final LegalEntityRepository legalEntities;
    private final BrandRepository brands;
    private final LocationRepository locations;
    private final ShiftTemplateRepository templates;
    private final ShiftRepository shifts;
    private final HrmPlatformAuthorizationService platformAuthorization;

    @Override
    public List<Organization> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        if (context != null && context.getOrgId() != null) {
            return repository.findByPlatformOrganizationId(context.getOrgId()).stream().toList();
        }
        return repository.findAll();
    }

    @Override
    public Organization findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Organization row = live(id);
        StaffingScope.assertSameOrg(context, row.getPlatformOrganizationId());
        return row;
    }

    @Override
    @Transactional
    public Organization create(Organization body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        body.setPlatformOrganizationId(StaffingScope.organizationId(context, body.getPlatformOrganizationId()));
        validate(body);
        if (repository.findByPlatformOrganizationId(body.getPlatformOrganizationId()).isPresent()) {
            throw StaffingRules.conflict("A profile already exists for this organization");
        }
        body.setId(null);
        body.markNew(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public Organization update(Organization body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Organization existing = live(body.getId());
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        body.setPlatformOrganizationId(existing.getPlatformOrganizationId());
        validate(body);
        body.setId(existing.getId());
        body.setCreatedAt(existing.getCreatedAt());
        body.setCreatedBy(existing.getCreatedBy());
        body.setIsDeleted(existing.getIsDeleted());
        body.markUpdated(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Organization existing = live(id);
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        UUID orgId = existing.getPlatformOrganizationId();
        if (!legalEntities.findByPlatformOrganizationId(orgId).isEmpty()
                || !brands.findByPlatformOrganizationId(orgId).isEmpty()
                || locations.existsByReferenceEntityAndReferenceId(ReferenceEntityKind.ORGANIZATION, orgId)
                || templates.findByPlatformOrganizationId(orgId).stream().findAny().isPresent()
                || shifts.existsByPlatformOrganizationId(orgId)) {
            throw StaffingRules.conflict("Organization profile still has legal entities, brands, locations, or shifts");
        }
        existing.markDeleted(StaffingScope.actor(context));
        repository.save(existing);
    }

    private Organization live(UUID id) {
        return repository.findById(id).filter(Organization::live).orElseThrow(() -> StaffingRules.notFound("Organization not found"));
    }

    private static void validate(Organization body) {
        StaffingRules.requireText(body.getName(), "name");
        StaffingRules.requireEmail(body.getEmail(), "email");
        StaffingRules.requireText(body.getPhone(), "phone");
        StaffingRules.requireTimezone(body.getTimezone());
        StaffingRules.requireCurrency(body.getCurrency());
        body.setCurrency(body.getCurrency().trim().toUpperCase());
    }
}
