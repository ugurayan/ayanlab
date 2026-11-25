package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.DressCode;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface DressCodeService {
    List<DressCode> findAll(CoreContext context);
    DressCode findById(UUID id, CoreContext context);
    DressCode create(DressCode body, CoreContext context);
    DressCode update(DressCode body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
