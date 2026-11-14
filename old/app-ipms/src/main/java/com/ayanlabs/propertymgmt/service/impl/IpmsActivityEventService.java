package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.propertymgmt.entity.platform.IpmsActivityEvent;
import com.ayanlabs.propertymgmt.repository.IpmsActivityEventRepository;
import com.ayanlabs.propertymgmt.service.IIpmsActivityEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class IpmsActivityEventService implements IIpmsActivityEventService {

    private final IpmsActivityEventRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<IpmsActivityEvent> findRecent(CoreContext context, Integer limit) {
        UUID appId = requireAppId(context);
        int max = limit == null || limit < 1 ? 20 : Math.min(limit, 100);
        List<IpmsActivityEvent> all = repository.findByAppIdAndIsActiveTrueOrderByOccurredAtDesc(appId);
        return all.size() <= max ? all : all.subList(0, max);
    }

    @Override
    @Transactional(readOnly = true)
    public IpmsActivityEvent findById(UUID id, CoreContext context) {
        return repository.findByIdAndAppId(id, requireAppId(context))
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Activity event not found"));
    }

    @Override
    @Transactional
    public IpmsActivityEvent create(IpmsActivityEvent item, CoreContext context) {
        item.setAppId(requireAppId(context));
        if (item.getOccurredAt() == null) {
            item.setOccurredAt(Instant.now());
        }
        if (item.getOrganizationId() == null && context.getOrgId() != null) {
            item.setOrganizationId(context.getOrgId());
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public IpmsActivityEvent update(IpmsActivityEvent item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "Activity event id required");
        }
        IpmsActivityEvent current = findById(item.getId(), context);
        item.setAppId(current.getAppId());
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        IpmsActivityEvent current = findById(id, context);
        current.setIsActive(false);
        map(current, context.getUserId(), ActionEnum.DELETE);
        repository.save(current);
    }

    private UUID requireAppId(CoreContext context) {
        if (context.getAppId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required in context");
        }
        return context.getAppId();
    }
}
