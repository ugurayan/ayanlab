package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.entity.ShiftRequirement;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.repository.ShiftRequirementRepository;
import com.ayanlab.hrm.backend.service.ShiftService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.util.StaffingRules;
import com.ayanlab.hrm.backend.util.StaffingScope;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private static final String PERM_READ = "staffiq:shifts:read";
    private static final String PERM_CREATE = "staffiq:shifts:create";
    private static final String PERM_UPDATE = "staffiq:shifts:update";
    private static final String PERM_DELETE = "staffiq:shifts:delete";
    private static final String OWNER = "INSTANCE";

    private final ShiftRepository repository;
    private final ShiftRequirementRepository requirements;
    private final LocationRepository locations;
    private final HrmPlatformAuthorizationService platformAuthorization;

    @Override
    public List<Shift> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        List<Shift> rows = context != null && context.getOrgId() != null
                ? repository.findByPlatformOrganizationId(context.getOrgId())
                : repository.findAll();
        rows.forEach(this::attach);
        return rows;
    }

    @Override
    public Shift findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Shift row = live(id);
        StaffingScope.assertSameOrg(context, row.getPlatformOrganizationId());
        attach(row);
        return row;
    }

    @Override
    @Transactional
    public Shift create(Shift body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        body.setPlatformOrganizationId(orgFromLocation(body.getLocationId(), context, body.getPlatformOrganizationId()));
        if (body.getStatus() == null || body.getStatus().isBlank()) {
            body.setStatus("OPEN");
        }
        StaffingRules.validateInstance(body, true);
        List<ShiftRequirement> kids = body.getRequirements() == null ? List.of() : List.copyOf(body.getRequirements());
        body.setId(null);
        body.markNew(StaffingScope.actor(context));
        Shift saved = repository.save(body);
        saved.setRequirements(saveRequirements(saved.getId(), kids, context));
        return saved;
    }

    @Override
    @Transactional
    public Shift update(Shift body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Shift existing = live(body.getId());
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        body.setPlatformOrganizationId(existing.getPlatformOrganizationId());
        body.setTemplateId(existing.getTemplateId());
        StaffingRules.validateInstance(body, false);
        body.setId(existing.getId());
        body.setCreatedAt(existing.getCreatedAt());
        body.setCreatedBy(existing.getCreatedBy());
        body.markUpdated(StaffingScope.actor(context));
        Shift saved = repository.save(body);
        if (body.getRequirements() != null) {
            requirements.deleteByOwnerKindAndOwnerId(OWNER, saved.getId());
            saved.setRequirements(saveRequirements(saved.getId(), body.getRequirements(), context));
        } else {
            attach(saved);
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Shift existing = live(id);
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        existing.markDeleted(StaffingScope.actor(context));
        repository.save(existing);
    }

    private UUID orgFromLocation(UUID locationId, CoreContext context, UUID requested) {
        Location location = locations.findById(locationId).filter(Location::live)
                .orElseThrow(() -> StaffingRules.bad("location does not exist"));
        UUID orgId = requested;
        if (location.getReferenceEntity() != null && location.getReferenceEntity().name().equals("ORGANIZATION")) {
            orgId = location.getReferenceId();
        }
        return StaffingScope.organizationId(context, orgId == null ? requested : orgId);
    }

    private List<ShiftRequirement> saveRequirements(UUID ownerId, List<ShiftRequirement> rows, CoreContext context) {
        List<ShiftRequirement> saved = new ArrayList<>();
        for (ShiftRequirement row : rows) {
            row.setId(null);
            row.setOwnerKind(OWNER);
            row.setOwnerId(ownerId);
            row.markNew(StaffingScope.actor(context));
            saved.add(requirements.save(row));
        }
        return saved;
    }

    private void attach(Shift row) {
        row.setRequirements(requirements.findByOwnerKindAndOwnerId(OWNER, row.getId()));
    }

    private Shift live(UUID id) {
        return repository.findById(id).filter(Shift::live).orElseThrow(() -> StaffingRules.notFound("Shift not found"));
    }
}
