package com.ayanlab.hrm.backend.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.Brand;
import java.util.List;
import java.util.UUID;

public interface BrandService {
    List<Brand> findAll(CoreContext context);
    Brand findById(UUID id, CoreContext context);
    Brand create(Brand body, CoreContext context);
    Brand update(Brand body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
