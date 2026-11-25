package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ContractType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ContractTypeRepository extends JpaRepository<ContractType, UUID> {
    List<ContractType> findByProfileId(UUID profileId);
    List<ContractType> findByOrgId(UUID orgId);
    List<ContractType> findByWorkerId(UUID workerId);
}
