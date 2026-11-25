package com.ayanlab.hrm.backend.util;

import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.entity.ShiftTemplate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class StaffingRules {

    public static final Set<String> DAYS = Set.of("sun", "mon", "tue", "wed", "thu", "fri", "sat");
    public static final Set<String> STAFFING = Set.of("SHORT_TERM", "LONG_TERM");
    public static final Set<String> WAGE = Set.of("HOURLY", "DAILY");
    public static final Set<String> STAFF_SELECTION = Set.of(
            "open-to-all", "open-to-new", "smart-assign", "request-approval", "top-pros-only");
    public static final Set<String> ATTIRE = Set.of("basic", "strict", "custom", "no-preference");
    public static final Set<String> GROOMING = Set.of("elevated", "professional", "standard", "custom", "no-preference");
    public static final Set<String> SHIFT_STATUS = Set.of("OPEN", "FILLED", "IN_PROGRESS", "COMPLETED", "CANCELLED");
    private static final long MAX_MINUTES = 12 * 60;

    private StaffingRules() {}

    public static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw bad(field + " is required");
        }
    }

    public static void requireEmail(String value, String field) {
        requireText(value, field);
        if (!value.contains("@")) {
            throw bad(field + " must contain @");
        }
    }

    public static void requireTimezone(String value) {
        requireText(value, "timezone");
        try {
            ZoneId.of(value.trim());
        } catch (java.time.DateTimeException ex) {
            throw bad("timezone is not a valid IANA id");
        }
    }

    public static void requireCurrency(String value) {
        requireText(value, "currency");
        if (!value.trim().matches("[A-Za-z]{3}")) {
            throw bad("currency must be a 3-letter ISO code");
        }
    }

    public static ReferenceEntityKind requireReferenceEntity(String raw) {
        requireText(raw, "referenceEntity");
        try {
            return ReferenceEntityKind.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw bad("referenceEntity is not supported");
        }
    }

    public static void requireUuid(Object value, String field) {
        if (value == null) {
            throw bad(field + " is required");
        }
    }

    public static long scheduleMinutes(String startTime, String endTime) {
        LocalTime start = parseTime(startTime, "startTime");
        LocalTime end = parseTime(endTime, "endTime");
        if (start.equals(end)) {
            throw bad("endTime must differ from startTime");
        }
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes <= 0) {
            minutes += 24 * 60;
        }
        if (minutes > MAX_MINUTES) {
            throw bad("shift duration must be at most 12 hours");
        }
        return minutes;
    }

    public static void requireBreak(Integer unpaidBreakMinutes, long durationMinutes) {
        int minutes = unpaidBreakMinutes == null ? -1 : unpaidBreakMinutes;
        if (minutes < 0 || minutes >= durationMinutes) {
            throw bad("unpaidBreakMinutes must be zero or greater and shorter than the shift");
        }
    }

    public static void requireWage(String wageType, Integer wageAmountMinor, Integer employeesNeeded) {
        if (wageType == null || !WAGE.contains(wageType)) {
            throw bad("wageType must be HOURLY or DAILY");
        }
        if (wageAmountMinor == null || wageAmountMinor <= 0) {
            throw bad("wageAmountMinor must be greater than zero");
        }
        if (employeesNeeded == null || employeesNeeded < 1) {
            throw bad("employeesNeeded must be at least 1");
        }
    }

    public static void requireOptionalMoney(BigDecimal tipPercent, Integer tipAmountMinor) {
        if (tipPercent != null && tipPercent.signum() < 0) {
            throw bad("tipPercent must be zero or greater");
        }
        if (tipAmountMinor != null && tipAmountMinor < 0) {
            throw bad("tipAmountMinor must be zero or greater");
        }
    }

    public static void validateTemplate(ShiftTemplate body) {
        requireText(body.getName(), "name");
        requireText(body.getDescription(), "description");
        requireUuid(body.getLocationId(), "locationId");
        requireUuid(body.getRoleId(), "roleId");
        if (body.getStaffingType() == null || !STAFFING.contains(body.getStaffingType())) {
            throw bad("staffingType must be SHORT_TERM or LONG_TERM");
        }
        if (body.getStaffSelectionType() == null || !STAFF_SELECTION.contains(body.getStaffSelectionType())) {
            throw bad("staffSelectionType is not supported");
        }
        if (body.getAttireRestrictionLevel() != null
                && !body.getAttireRestrictionLevel().isBlank()
                && !ATTIRE.contains(body.getAttireRestrictionLevel())) {
            throw bad("attireRestrictionLevel is not supported");
        }
        if (body.getGroomingLevel() != null && !body.getGroomingLevel().isBlank() && !GROOMING.contains(body.getGroomingLevel())) {
            throw bad("groomingLevel is not supported");
        }
        long minutes = scheduleMinutes(body.getStartTime(), body.getEndTime());
        requireBreak(body.getUnpaidBreakMinutes(), minutes);
        requireWage(body.getWageType(), body.getWageAmountMinor(), body.getEmployeesNeeded());
        requireCurrency(body.getCurrency());
        requireOptionalMoney(body.getTipPercent(), body.getTipAmountMinor());
        if (body.isRecurring()) {
            requireDays(body.getDaysOfWeek());
        }
    }

    public static void validateInstance(Shift body, boolean creating) {
        requireText(body.getName(), "name");
        requireText(body.getDescription(), "description");
        requireUuid(body.getLocationId(), "locationId");
        requireUuid(body.getRoleId(), "roleId");
        if (body.getStaffSelectionType() == null || !STAFF_SELECTION.contains(body.getStaffSelectionType())) {
            throw bad("staffSelectionType is not supported");
        }
        if (body.getStartsAt() == null || body.getEndsAt() == null || !body.getEndsAt().isAfter(body.getStartsAt())) {
            throw bad("endsAt must be after startsAt");
        }
        long minutes = Duration.between(body.getStartsAt(), body.getEndsAt()).toMinutes();
        if (minutes > MAX_MINUTES) {
            throw bad("shift duration must be at most 12 hours");
        }
        requireBreak(body.getUnpaidBreakMinutes(), minutes);
        requireWage(body.getWageType(), body.getWageAmountMinor(), body.getEmployeesNeeded());
        requireCurrency(body.getCurrency());
        requireOptionalMoney(body.getTipPercent(), body.getTipAmountMinor());
        if (body.getStatus() == null || body.getStatus().isBlank()) {
            body.setStatus("OPEN");
        }
        if (!SHIFT_STATUS.contains(body.getStatus())) {
            throw bad("status is not supported");
        }
        if (creating && "OPEN".equals(body.getStatus()) && body.getStartsAt().isBefore(Instant.now().plus(Duration.ofMinutes(30)))) {
            throw bad("an open shift must start at least 30 minutes from now");
        }
    }

    public static void requireDays(String daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isBlank()) {
            throw bad("daysOfWeek is required when recurring");
        }
        for (String day : daysOfWeek.split(",")) {
            if (!DAYS.contains(day.trim().toLowerCase(Locale.ROOT))) {
                throw bad("daysOfWeek contains an unknown day");
            }
        }
    }

    public static LocalTime parseTime(String value, String field) {
        requireText(value, field);
        try {
            return LocalTime.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw bad(field + " must be HH:mm");
        }
    }

    public static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    public static ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
