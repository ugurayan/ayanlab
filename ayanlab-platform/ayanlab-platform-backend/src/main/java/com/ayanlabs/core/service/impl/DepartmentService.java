package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.Department;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.DepartmentRepository;
import com.ayanlabs.core.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public Department create(Department item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return departmentRepository.save(item);
    }

    @Override
    @Transactional
    public Department update(Department item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Department id required");
        if (departmentRepository.findById(item.getId()).isEmpty()) {
            throw new CoreException("NOT_FOUND", "Department not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return departmentRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Department item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        departmentRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Department d = findById(id, context);
        if (d != null) delete(d, context);
    }

    @Override
    public Department findById(UUID id, CoreContext context) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Department> findByOrganizationId(UUID organizationId, CoreContext context) {
        if (organizationId == null) throw new CoreException("VALIDATION_ERROR", "organizationId required");
        return departmentRepository.findByOrganizationIdAndIsActiveTrueOrderByNameAsc(organizationId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        Department found = findById(id, context);
        if (found != null) {
            departmentRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public Department setActive(UUID id, boolean active, CoreContext context) {
        Department found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "Department not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return departmentRepository.save(found);
    }
}