package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.provider.AppSecurityContext;
import com.ayanlabs.propertymgmt.entity.rentals.PropertyType;
import com.ayanlabs.propertymgmt.repository.PropertyTypeRepository;
import com.ayanlabs.propertymgmt.service.IPropertyTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class PropertyTypeService implements IPropertyTypeService {

    private static final Set<String> MANAGEMENT_ADMIN_ROLES = Set.of(
        "SYSTEM_ADMINISTRATOR",
        "ORGANIZATION_ADMINISTRATOR",
        "PROPERTY_MANAGER"
    );

    private final PropertyTypeRepository propertyTypeRepository;

    @Override
    @Transactional
    public PropertyType create(PropertyType item, CoreContext context) {
        requireManagementAdmin();
        UUID orgId = requireOrgId(context);
        if (Boolean.TRUE.equals(item.getIsSystemDefault())) {
            throw new CoreException("FORBIDDEN", "Cannot create system default property types via API");
        }
        item.setOrganizationId(orgId);
        item.setIsSystemDefault(false);
        assertCodeAvailable(orgId, item.getCode(), null);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return propertyTypeRepository.save(item);
    }

    @Override
    @Transactional
    public PropertyType update(PropertyType item, CoreContext context) {
        requireManagementAdmin();
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "PropertyType id required");
        }
        PropertyType current = propertyTypeRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "PropertyType not found"));
        assertCustomTypeMutable(current, context);
        item.setOrganizationId(current.getOrganizationId());
        item.setIsSystemDefault(false);
        if (item.getCode() != null && !item.getCode().equals(current.getCode())) {
            assertCodeAvailable(current.getOrganizationId(), item.getCode(), current.getId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return propertyTypeRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        requireManagementAdmin();
        PropertyType current = propertyTypeRepository.findById(id)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "PropertyType not found"));
        assertCustomTypeMutable(current, context);
        current.setIsActive(false);
        map(current, context.getUserId(), ActionEnum.DELETE);
        propertyTypeRepository.save(current);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyType findById(UUID id, CoreContext context) {
        PropertyType type = propertyTypeRepository.findById(id)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "PropertyType not found"));
        if (!isVisible(type, context.getOrgId())) {
            throw new CoreException("FORBIDDEN", "PropertyType not visible for current organization");
        }
        return type;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyType> findVisible(CoreContext context) {
        UUID orgId = context.getOrgId();
        if (orgId == null) {
            return propertyTypeRepository.findSystemDefaults();
        }
        return propertyTypeRepository.findVisibleForOrganization(orgId);
    }

    private void requireManagementAdmin() {
        AppSecurityContext.AppContext security = AppSecurityContext.get();
        if (security == null || !security.hasAnyRole(MANAGEMENT_ADMIN_ROLES.toArray(String[]::new))) {
            throw new CoreException("FORBIDDEN", "Property management admin role required");
        }
    }

    private UUID requireOrgId(CoreContext context) {
        if (context.getOrgId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "orgId required in context for custom property types");
        }
        return context.getOrgId();
    }

    private void assertCustomTypeMutable(PropertyType type, CoreContext context) {
        if (Boolean.TRUE.equals(type.getIsSystemDefault()) || type.getOrganizationId() == null) {
            throw new CoreException("FORBIDDEN", "System default property types cannot be modified");
        }
        if (context.getOrgId() == null || !type.getOrganizationId().equals(context.getOrgId())) {
            throw new CoreException("FORBIDDEN", "PropertyType belongs to a different organization");
        }
    }

    private void assertCodeAvailable(UUID organizationId, String code, UUID excludeId) {
        if (code == null || code.isBlank()) {
            throw new CoreException("INVALID_ARGUMENT", "code required");
        }
        propertyTypeRepository.findByOrganizationIdAndCode(organizationId, code).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new CoreException("CONFLICT", "PropertyType code already exists for organization");
            }
        });
    }

    private boolean isVisible(PropertyType type, UUID orgId) {
        if (!Boolean.TRUE.equals(type.getIsActive())) {
            return false;
        }
        if (type.getOrganizationId() == null) {
            return true;
        }
        return orgId != null && orgId.equals(type.getOrganizationId());
    }
}
