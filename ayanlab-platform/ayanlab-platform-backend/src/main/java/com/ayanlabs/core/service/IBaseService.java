package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;

import java.util.UUID;

public interface IBaseService<T> {
    T create(T item, CoreContext context);

    T update(T item, CoreContext context);

    void delete(T item, CoreContext context);

    /** Soft delete: {@code isActive=false}. */
    void deleteById(UUID id, CoreContext context);

    /** Permanent row removal. */
    void hardDeleteById(UUID id, CoreContext context);

    T setActive(UUID id, boolean active, CoreContext context);

    T findById(UUID id, CoreContext context);
}
