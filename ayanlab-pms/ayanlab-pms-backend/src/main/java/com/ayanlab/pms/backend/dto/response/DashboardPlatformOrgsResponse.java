package com.ayanlab.pms.backend.dto.response;

import java.util.List;

public record DashboardPlatformOrgsResponse(
        long total,
        List<OrgTypeCount> byType
) {
    public record OrgTypeCount(String type, long count) {
    }
}
