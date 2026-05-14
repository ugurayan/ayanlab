package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.App;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.AppRepository;
import com.ayanlabs.core.service.IAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppService implements IAppService {

    private final AppRepository appRepository;

    @Override
    @Transactional
    public App create(App item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return appRepository.save(item);
    }

    @Override
    @Transactional
    public App update(App item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "App id required");
        App current = appRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "App not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return appRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(App item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        appRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        App a = findById(id, context);
        if (a != null) delete(a, context);
    }

    @Override
    public App findById(UUID id, CoreContext context) {
        try {
            return appRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public PagedResponse<App> findAll(int page, int size, CoreContext context) {
        return findAll(page, size, null, "", "", context);
    }

    @Override
    public PagedResponse<App> findAll(int page, int size, UUID appId, String code, String name, CoreContext context) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        String codeFilter = (code == null) ? "" : code;
        String nameFilter = (name == null) ? "" : name;
        org.springframework.data.domain.Page<App> p;
        if (codeFilter.isEmpty() && nameFilter.isEmpty()) {
            p = appRepository.findAll(pageable);
        } else if (!codeFilter.isEmpty() && !nameFilter.isEmpty()) {
            p = appRepository.findByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(codeFilter, nameFilter, pageable);
        } else if (!codeFilter.isEmpty()) {
            p = appRepository.findByCodeContainingIgnoreCase(codeFilter, pageable);
        } else {
            p = appRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        }
        PagedResponse.PagedResponseBuilder<App> builder = PagedResponse.<App>builder();
        PagedResponse<App> resp = builder.content(p.getContent()).page(p.getNumber()).size(p.getSize()).totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast()).build();
        return resp;
    }
}
