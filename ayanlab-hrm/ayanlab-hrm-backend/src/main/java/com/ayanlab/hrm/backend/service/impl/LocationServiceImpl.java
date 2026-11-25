package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.Brand;
import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.repository.BrandRepository;
import com.ayanlab.hrm.backend.repository.LegalEntityRepository;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.repository.ShiftTemplateRepository;
import com.ayanlab.hrm.backend.service.LocationService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.util.StaffingRules;
import com.ayanlab.hrm.backend.util.StaffingScope;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private static final String PERM_READ = "staffiq:locations:read";
    private static final String PERM_CREATE = "staffiq:locations:create";
    private static final String PERM_UPDATE = "staffiq:locations:update";
    private static final String PERM_DELETE = "staffiq:locations:delete";

    private final LocationRepository repository;
    private final OrganizationRepository organizations;
    private final BrandRepository brands;
    private final LegalEntityRepository legalEntities;
    private final ShiftRepository shifts;
    private final ShiftTemplateRepository templates;
    private final HrmPlatformAuthorizationService platformAuthorization;

    @Override
    public List<Location> findAll(CoreContext context, String referenceEntity, UUID referenceId) {
        platformAuthorization.requirePermission(context, PERM_READ);
        if (referenceEntity != null && !referenceEntity.isBlank() && referenceId != null) {
            ReferenceEntityKind kind = StaffingRules.requireReferenceEntity(referenceEntity);
            List<Location> rows = repository.findByReferenceEntityAndReferenceId(kind, referenceId);
            rows.forEach(row -> assertVisible(context, row));
            return rows;
        }
        if (context != null && context.getOrgId() != null) {
            Map<UUID, Location> merged = new LinkedHashMap<>();
            repository.findByReferenceEntityAndReferenceId(ReferenceEntityKind.ORGANIZATION, context.getOrgId())
                    .forEach(row -> merged.put(row.getId(), row));
            List<UUID> brandIds = brands.findByPlatformOrganizationId(context.getOrgId()).stream().map(Brand::getId).toList();
            if (!brandIds.isEmpty()) {
                repository.findByReferenceEntityAndReferenceIdIn(ReferenceEntityKind.BRAND, brandIds)
                        .forEach(row -> merged.put(row.getId(), row));
            }
            return new ArrayList<>(merged.values());
        }
        return repository.findAll();
    }

    @Override
    public Location findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Location row = live(id);
        assertVisible(context, row);
        return row;
    }

    @Override
    @Transactional
    public Location create(Location body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        validate(body, context);
        body.setId(null);
        body.markNew(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public Location update(Location body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Location existing = live(body.getId());
        assertVisible(context, existing);
        validate(body, context);
        body.setId(existing.getId());
        body.setCreatedAt(existing.getCreatedAt());
        body.setCreatedBy(existing.getCreatedBy());
        body.markUpdated(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Location existing = live(id);
        assertVisible(context, existing);
        if (shifts.existsByLocationId(id) || templates.existsByLocationId(id)) {
            throw StaffingRules.conflict("Location still has shifts");
        }
        existing.markDeleted(StaffingScope.actor(context));
        repository.save(existing);
    }

    private void validate(Location body, CoreContext context) {
        StaffingRules.requireText(body.getName(), "name");
        StaffingRules.requireText(body.getFormattedAddress(), "formattedAddress");
        StaffingRules.requireUuid(body.getReferenceId(), "referenceId");
        if (body.getReferenceEntity() == null) {
            throw StaffingRules.bad("referenceEntity is required");
        }
        if (body.getTimezone() != null && !body.getTimezone().isBlank()) {
            StaffingRules.requireTimezone(body.getTimezone());
        }
        switch (body.getReferenceEntity()) {
            case ORGANIZATION -> {
                StaffingScope.organizationId(context, body.getReferenceId());
                if (organizations.findByPlatformOrganizationId(body.getReferenceId()).isEmpty()) {
                    throw StaffingRules.bad("organization profile does not exist");
                }
            }
            case BRAND -> {
                var brand = brands.findById(body.getReferenceId()).filter(row -> row.live())
                        .orElseThrow(() -> StaffingRules.bad("brand does not exist"));
                StaffingScope.assertSameOrg(context, brand.getPlatformOrganizationId());
            }
            case LEGAL_ENTITY -> {
                var legal = legalEntities.findById(body.getReferenceId()).filter(row -> row.live())
                        .orElseThrow(() -> StaffingRules.bad("legal entity does not exist"));
                StaffingScope.assertSameOrg(context, legal.getPlatformOrganizationId());
            }
            case PROFILE, USER, BANK -> {
                // Accepted when referenceId is a UUID. Other services are not queried in this delivery.
            }
            default -> throw StaffingRules.bad("referenceEntity is not supported");
        }
    }

    private void assertVisible(CoreContext context, Location row) {
        if (context == null || context.getOrgId() == null || row.getReferenceEntity() == null) {
            return;
        }
        if (row.getReferenceEntity() == ReferenceEntityKind.ORGANIZATION) {
            StaffingScope.assertSameOrg(context, row.getReferenceId());
            return;
        }
        if (row.getReferenceEntity() == ReferenceEntityKind.BRAND) {
            brands.findById(row.getReferenceId()).ifPresent(brand -> StaffingScope.assertSameOrg(context, brand.getPlatformOrganizationId()));
            return;
        }
        if (row.getReferenceEntity() == ReferenceEntityKind.LEGAL_ENTITY) {
            legalEntities.findById(row.getReferenceId()).ifPresent(legal -> StaffingScope.assertSameOrg(context, legal.getPlatformOrganizationId()));
        }
    }

    private Location live(UUID id) {
        return repository.findById(id).filter(Location::live).orElseThrow(() -> StaffingRules.notFound("Location not found"));
    }
}
