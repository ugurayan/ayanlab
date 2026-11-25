package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.Brand;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.repository.BrandRepository;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private static final String READ = "staffiq:brands:read";
    private static final String CREATE = "staffiq:brands:create";
    private static final String UPDATE = "staffiq:brands:update";
    private static final String DELETE = "staffiq:brands:delete";

    private final BrandRepository repository;
    private final OrganizationRepository organizations;
    private final LocationRepository locations;
    private final HrmPlatformAuthorizationService platformAuthorization;

    @Override
    public List<Brand> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, READ);
        if (context != null && context.getOrgId() != null) {
            return repository.findByPlatformOrganizationId(context.getOrgId());
        }
        return repository.findAll();
    }

    @Override
    public Brand findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, READ);
        Brand row = live(id);
        StaffingScope.assertSameOrg(context, row.getPlatformOrganizationId());
        return row;
    }

    @Override
    @Transactional
    public Brand create(Brand body, CoreContext context) {
        platformAuthorization.requirePermission(context, CREATE);
        body.setPlatformOrganizationId(StaffingScope.organizationId(context, body.getPlatformOrganizationId()));
        if (organizations.findByPlatformOrganizationId(body.getPlatformOrganizationId()).isEmpty()) {
            throw StaffingRules.bad("organization profile does not exist");
        }
        validate(body);
        body.setId(null);
        body.markNew(StaffingScope.actor(context));
        return repository.save(body);
    }

    @Override
    @Transactional
    public Brand update(Brand body, CoreContext context) {
        platformAuthorization.requirePermission(context, UPDATE);
        Brand existing = live(body.getId());
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
        Brand existing = live(id);
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        if (locations.existsByReferenceEntityAndReferenceId(ReferenceEntityKind.BRAND, existing.getId())) {
            throw StaffingRules.conflict("Brand still has locations");
        }
        existing.markDeleted(StaffingScope.actor(context));
        repository.save(existing);
    }

    private Brand live(UUID id) {
        return repository.findById(id).filter(Brand::live).orElseThrow(() -> StaffingRules.notFound("Brand not found"));
    }

    private static void validate(Brand body) {
        StaffingRules.requireText(body.getName(), "name");
        StaffingRules.requireText(body.getArea(), "area");
    }
}
