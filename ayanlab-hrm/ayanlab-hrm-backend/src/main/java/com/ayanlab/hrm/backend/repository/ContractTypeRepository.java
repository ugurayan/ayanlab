package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ContractType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractTypeRepository extends JpaRepository<ContractType, String> {
    List<ContractType> findByProfileId(String profileId);
    List<ContractType> findByOrgId(String orgId);
    List<ContractType> findByWorkerId(String workerId);
}
