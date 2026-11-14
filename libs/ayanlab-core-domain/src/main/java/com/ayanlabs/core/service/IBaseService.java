package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;

import java.util.UUID;

public interface IBaseService<T> {
    T create(T item, CoreContext context);

    T update(T item, CoreContext context);

    void delete(T item, CoreContext context);

    void deleteById(UUID id, CoreContext context);

    T findById(UUID id, CoreContext context);
}
