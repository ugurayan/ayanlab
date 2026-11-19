package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.MeterReading;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterReadingRepository extends IpmsOrgRepository<MeterReading, java.util.UUID> {
}
