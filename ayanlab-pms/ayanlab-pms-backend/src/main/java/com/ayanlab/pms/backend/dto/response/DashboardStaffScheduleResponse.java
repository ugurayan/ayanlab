package com.ayanlab.pms.backend.dto.response;

import java.util.List;

public record DashboardStaffScheduleResponse(
        List<ScheduleItem> items
) {
    public record ScheduleItem(String time, String label, String type) {
    }
}
