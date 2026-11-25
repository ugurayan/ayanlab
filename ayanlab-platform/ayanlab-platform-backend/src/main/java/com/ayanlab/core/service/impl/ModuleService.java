package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.Module;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ModuleRepository;
import com.ayanlab.core.service.IModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleService implements IModuleService {

    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public Module create(Module item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return moduleRepository.save(item);
    }

    @Override
    @Transactional
    public Module update(Module item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Module id required");
        Module current = moduleRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "Module not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return moduleRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Module item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        moduleRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Module m = findById(id, context);
        if (m != null) delete(m, context);
    }

    @Override
    public Module findById(UUID id, CoreContext context) {
        try {
            return moduleRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public PagedResponse<Module> findAll(int page, int size, CoreContext context) {
        return findAll(page, size, null, "", "", context);
    }

    @Override
    public PagedResponse<Module> findAll(int page, int size, UUID appId, String code, String name, CoreContext context) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        String codeFilter = (code == null) ? "" : code;
        String nameFilter = (name == null) ? "" : name;
        org.springframework.data.domain.Page<Module> p;
        if (codeFilter.isEmpty() && nameFilter.isEmpty()) {
            p = moduleRepository.findAll(pageable);
        } else if (!codeFilter.isEmpty() && !nameFilter.isEmpty()) {
            p = moduleRepository.findByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(codeFilter, nameFilter, pageable);
        } else if (!codeFilter.isEmpty()) {
            p = moduleRepository.findByCodeContainingIgnoreCase(codeFilter, pageable);
        } else {
            p = moduleRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        }
        PagedResponse.PagedResponseBuilder<Module> builder = PagedResponse.<Module>builder();
        PagedResponse<Module> resp = builder.content(p.getContent()).page(p.getNumber()).size(p.getSize()).totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast()).build();
        return resp;
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        Module found = findById(id, context);
        if (found != null) {
            moduleRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public Module setActive(UUID id, boolean active, CoreContext context) {
        Module found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "Module not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return moduleRepository.save(found);
    }
}
