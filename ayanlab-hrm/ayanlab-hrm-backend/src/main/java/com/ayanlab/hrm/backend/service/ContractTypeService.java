package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ContractType;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ContractTypeService {
    List<ContractType> findAll(CoreContext context);
    ContractType findById(String id, CoreContext context);
    ContractType create(ContractType body, CoreContext context);
    ContractType update(ContractType body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
