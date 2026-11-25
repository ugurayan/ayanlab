package com.ayanlab.hrm.backend.service.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.entity.ShiftTemplate;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.ayanlab.hrm.backend.util.StaffingRules;
class StaffingRulesTest {

    @Test
    void nightShiftIsValidWhenItLastsAtMostTwelveHours() {
        assertThat(StaffingRules.scheduleMinutes("22:00", "06:00")).isEqualTo(8 * 60);
    }

    @Test
    void equalTimesAndLongShiftsAreRejected() {
        assertThatThrownBy(() -> StaffingRules.scheduleMinutes("08:00", "08:00"))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> StaffingRules.scheduleMinutes("08:00", "22:00"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void breakCannotCoverTheWholeShift() {
        assertThatThrownBy(() -> StaffingRules.requireBreak(480, 480))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void openShiftMustStartAtLeastThirtyMinutesAhead() {
        Shift shift = sampleShift();
        shift.setStartsAt(Instant.now().plusSeconds(60));
        shift.setEndsAt(shift.getStartsAt().plusSeconds(3600));
        assertThatThrownBy(() -> StaffingRules.validateInstance(shift, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("30 minutes");
    }

    @Test
    void recurringTemplateRequiresDays() {
        ShiftTemplate template = new ShiftTemplate();
        template.setName("AM Dishwasher");
        template.setDescription("Wash");
        template.setLocationId(java.util.UUID.randomUUID());
        template.setRoleId(java.util.UUID.randomUUID());
        template.setStaffingType("SHORT_TERM");
        template.setStaffSelectionType("smart-assign");
        template.setStartTime("09:00");
        template.setEndTime("17:00");
        template.setUnpaidBreakMinutes(30);
        template.setWageType("HOURLY");
        template.setWageAmountMinor(2500);
        template.setCurrency("CHF");
        template.setEmployeesNeeded(1);
        template.setRecurring(true);
        assertThatThrownBy(() -> StaffingRules.validateTemplate(template))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("daysOfWeek");
    }

    private static Shift sampleShift() {
        Shift shift = new Shift();
        shift.setName("Dinner");
        shift.setDescription("Service");
        shift.setLocationId(java.util.UUID.randomUUID());
        shift.setRoleId(java.util.UUID.randomUUID());
        shift.setStaffSelectionType("open-to-all");
        shift.setUnpaidBreakMinutes(0);
        shift.setWageType("HOURLY");
        shift.setWageAmountMinor(2000);
        shift.setCurrency("CHF");
        shift.setEmployeesNeeded(1);
        shift.setStatus("OPEN");
        return shift;
    }
}
