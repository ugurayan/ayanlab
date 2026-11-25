package com.ayanlab.hrm.backend.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.LegalEntity;
import java.util.List;
import java.util.UUID;

public interface LegalEntityService {
    List<LegalEntity> findAll(CoreContext context);
    LegalEntity findById(UUID id, CoreContext context);
    LegalEntity create(LegalEntity body, CoreContext context);
    LegalEntity update(LegalEntity body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
