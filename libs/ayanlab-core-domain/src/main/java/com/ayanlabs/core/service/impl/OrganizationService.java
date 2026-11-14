package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.Organization;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.OrganizationRepository;
import com.ayanlabs.core.service.IOrganizationService;
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
public class OrganizationService implements IOrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public Organization create(Organization item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return organizationRepository.save(item);
    }

    @Override
    @Transactional
    public Organization update(Organization item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Organization id required");
        Organization current = organizationRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "Organization not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return organizationRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Organization item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        organizationRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Organization o = findById(id, context);
        if (o != null) delete(o, context);
    }

    @Override
    public Organization findById(UUID id, CoreContext context) {
        try {
            return organizationRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
    }

    @Override
    public PagedResponse<Organization> findAll(int page, int size, CoreContext context) {
        return findAll(page, size, null, "", "", context);
    }

    @Override
    public PagedResponse<Organization> findAll(int page, int size, UUID appId, String code, String name, CoreContext context) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        String codeFilter = (code == null) ? "" : code;
        String nameFilter = (name == null) ? "" : name;
        org.springframework.data.domain.Page<Organization> p;
        if (codeFilter.isEmpty() && nameFilter.isEmpty()) {
            p = organizationRepository.findAll(pageable);
        } else if (!codeFilter.isEmpty() && !nameFilter.isEmpty()) {
            p = organizationRepository.findByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(codeFilter, nameFilter, pageable);
        } else if (!codeFilter.isEmpty()) {
            p = organizationRepository.findByCodeContainingIgnoreCase(codeFilter, pageable);
        } else {
            p = organizationRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        }
        PagedResponse.PagedResponseBuilder<Organization> builder = PagedResponse.<Organization>builder();
        PagedResponse<Organization> resp = builder.content(p.getContent()).page(p.getNumber()).size(p.getSize()).totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast()).build();
        return resp;
    }
}
