package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.DressCode;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface DressCodeService {
    List<DressCode> findAll(CoreContext context);
    DressCode findById(String id, CoreContext context);
    DressCode create(DressCode body, CoreContext context);
    DressCode update(DressCode body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
