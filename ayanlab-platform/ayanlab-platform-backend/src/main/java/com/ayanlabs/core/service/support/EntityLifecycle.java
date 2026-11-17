package com.ayanlabs.core.service.support;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.data.mapper.BaseMapper;
import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.core.exception.CoreException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public final class EntityLifecycle {

    private EntityLifecycle() {}

    public static <T extends BaseEntity> T require(T entity, String label) {
        if (entity == null) {
            throw new CoreException("NOT_FOUND", label + " not found");
        }
        return entity;
    }

    public static <T extends BaseEntity> T softDelete(T entity, CoreContext context, JpaRepository<T, UUID> repo) {
        T marked = BaseMapper.map(entity, context.getUserId(), ActionEnum.DELETE);
        marked.setIsActive(false);
        return repo.save(marked);
    }

    public static <T extends BaseEntity> void hardDelete(T entity, JpaRepository<T, UUID> repo) {
        repo.delete(require(entity, "Record"));
    }

    public static <T extends BaseEntity> T setActive(
            T entity, boolean active, CoreContext context, JpaRepository<T, UUID> repo) {
        T current = require(entity, "Record");
        current.setIsActive(active);
        current = BaseMapper.map(current, context.getUserId(), ActionEnum.UPDATE);
        current.setIsActive(active);
        return repo.save(current);
    }
}
