package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.repository.IpmsOrgRepository;
import com.ayanlab.pms.backend.service.IIpmsOrgScopedService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

public abstract class AbstractIpmsOrgService<T extends BaseEntity & OrgScoped, R extends IpmsOrgRepository<T, UUID>>
        implements IIpmsOrgScopedService<T> {

    protected final R repository;

    protected AbstractIpmsOrgService(R repository) {
        this.repository = repository;
    }

    protected abstract String entityLabel();

    @Override
    @Transactional
    public T create(T item, CoreContext context) {
        UUID orgId = requireOrgId(context);
        item.setOrganizationId(orgId);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public T update(T item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", entityLabel() + " id required");
        }
        T current = findById(item.getId(), context);
        item.setOrganizationId(current.getOrganizationId());
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public void delete(T item, CoreContext context) {
        assertSameOrg(item, context);
        item.setIsActive(false);
        map(item, context.getUserId(), ActionEnum.DELETE);
        repository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        delete(findById(id, context), context);
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(UUID id, CoreContext context) {
        UUID orgId = requireOrgId(context);
        return repository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new CoreException("NOT_FOUND", entityLabel() + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(CoreContext context) {
        return repository.findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(requireOrgId(context));
    }

    protected UUID requireOrgId(CoreContext context) {
        if (context.getOrgId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "orgId required in context");
        }
        return context.getOrgId();
    }

    protected void assertSameOrg(T item, CoreContext context) {
        UUID orgId = requireOrgId(context);
        if (item.getOrganizationId() == null || !orgId.equals(item.getOrganizationId())) {
            throw new CoreException("FORBIDDEN", entityLabel() + " belongs to a different organization");
        }
    }
}
