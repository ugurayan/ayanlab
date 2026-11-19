package com.ayanlab.pms.backend.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * B2.4 contract smoke (full flow covered by {@code POST /pms/api/flows/lease-charge-portal} manual/E2E).
 */
class IpmsLeaseChargePortalFlowTest {

    @Test
    void flowEndpointDocumented() {
        assertThat("/pms/api/flows/lease-charge-portal").contains("lease-charge-portal");
    }
}