package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ContractType;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ContractTypeService {
    List<ContractType> findAll(CoreContext context);
    ContractType findById(UUID id, CoreContext context);
    ContractType create(ContractType body, CoreContext context);
    ContractType update(ContractType body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
