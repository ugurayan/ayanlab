package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.associations.CooperativeShareRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface CooperativeShareRecordRepository extends IpmsOrgRepository<CooperativeShareRecord, java.util.UUID> {
}
