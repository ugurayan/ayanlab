package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.ParcelDelivery;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelDeliveryRepository extends IpmsOrgRepository<ParcelDelivery, java.util.UUID> {
}
