package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.dto.request.GenerateShiftInstancesRequest;
import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.entity.ShiftRequirement;
import com.ayanlab.hrm.backend.entity.ShiftTemplate;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.repository.ShiftRequirementRepository;
import com.ayanlab.hrm.backend.repository.ShiftTemplateRepository;
import com.ayanlab.hrm.backend.service.ShiftTemplateService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.util.StaffingRules;
import com.ayanlab.hrm.backend.util.StaffingScope;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShiftTemplateServiceImpl implements ShiftTemplateService {

    private static final String READ = "staffiq:shift_templates:read";
    private static final String CREATE = "staffiq:shift_templates:create";
    private static final String UPDATE = "staffiq:shift_templates:update";
    private static final String DELETE = "staffiq:shift_templates:delete";
    private static final String OWNER = "TEMPLATE";

    private final ShiftTemplateRepository repository;
    private final ShiftRequirementRepository requirements;
    private final ShiftRepository shifts;
    private final LocationRepository locations;
    private final OrganizationRepository organizations;
    private final HrmPlatformAuthorizationService platformAuthorization;

    @Override
    public List<ShiftTemplate> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, READ);
        List<ShiftTemplate> rows = context != null && context.getOrgId() != null
                ? repository.findByPlatformOrganizationId(context.getOrgId())
                : repository.findAll();
        rows.forEach(this::attach);
        return rows;
    }

    @Override
    public ShiftTemplate findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, READ);
        ShiftTemplate row = live(id);
        StaffingScope.assertSameOrg(context, row.getPlatformOrganizationId());
        attach(row);
        return row;
    }

    @Override
    @Transactional
    public ShiftTemplate create(ShiftTemplate body, CoreContext context) {
        platformAuthorization.requirePermission(context, CREATE);
        body.setPlatformOrganizationId(resolveOrg(body.getLocationId(), context, body.getPlatformOrganizationId()));
        StaffingRules.validateTemplate(body);
        List<ShiftRequirement> kids = body.getRequirements() == null ? List.of() : List.copyOf(body.getRequirements());
        body.setId(null);
        body.markNew(StaffingScope.actor(context));
        ShiftTemplate saved = repository.save(body);
        saved.setRequirements(saveRequirements(OWNER, saved.getId(), kids, context));
        return saved;
    }

    @Override
    @Transactional
    public ShiftTemplate update(ShiftTemplate body, CoreContext context) {
        platformAuthorization.requirePermission(context, UPDATE);
        ShiftTemplate existing = live(body.getId());
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        body.setPlatformOrganizationId(existing.getPlatformOrganizationId());
        body.setLocationId(body.getLocationId() == null ? existing.getLocationId() : body.getLocationId());
        StaffingRules.validateTemplate(body);
        body.setId(existing.getId());
        body.setCreatedAt(existing.getCreatedAt());
        body.setCreatedBy(existing.getCreatedBy());
        body.markUpdated(StaffingScope.actor(context));
        ShiftTemplate saved = repository.save(body);
        if (body.getRequirements() != null) {
            requirements.deleteByOwnerKindAndOwnerId(OWNER, saved.getId());
            saved.setRequirements(saveRequirements(OWNER, saved.getId(), body.getRequirements(), context));
        } else {
            attach(saved);
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, DELETE);
        ShiftTemplate existing = live(id);
        StaffingScope.assertSameOrg(context, existing.getPlatformOrganizationId());
        for (Shift shift : shifts.findByTemplateId(id)) {
            shift.setTemplateId(null);
            shift.markUpdated(StaffingScope.actor(context));
            shifts.save(shift);
        }
        existing.markDeleted(StaffingScope.actor(context));
        repository.save(existing);
    }

    @Override
    @Transactional
    public List<Shift> generate(UUID id, GenerateShiftInstancesRequest request, CoreContext context) {
        platformAuthorization.requirePermission(context, CREATE);
        ShiftTemplate template = findById(id, context);
        Location location = locations.findById(template.getLocationId()).filter(Location::live)
                .orElseThrow(() -> StaffingRules.bad("location does not exist"));
        ZoneId zone = zone(location, template.getPlatformOrganizationId());
        List<LocalDate> dates = dates(template, request);
        LocalTime start = StaffingRules.parseTime(template.getStartTime(), "startTime");
        LocalTime end = StaffingRules.parseTime(template.getEndTime(), "endTime");
        List<Shift> created = new ArrayList<>();
        for (LocalDate date : dates) {
            ZonedDateTime startAt = ZonedDateTime.of(date, start, zone);
            ZonedDateTime endAt = ZonedDateTime.of(date, end, zone);
            if (!endAt.isAfter(startAt)) {
                endAt = endAt.plusDays(1);
            }
            if (shifts.findByTemplateIdAndLocationIdAndStartsAt(template.getId(), location.getId(), startAt.toInstant()).isPresent()) {
                continue;
            }
            Shift shift = copy(template, location.getId(), startAt.toInstant(), endAt.toInstant());
            shift.markNew(StaffingScope.actor(context));
            StaffingRules.validateInstance(shift, true);
            Shift saved = shifts.save(shift);
            saved.setRequirements(copyRequirements(template.getRequirements(), saved.getId(), context));
            created.add(saved);
        }
        return created;
    }

    private Shift copy(ShiftTemplate template, UUID locationId, java.time.Instant startsAt, java.time.Instant endsAt) {
        Shift shift = new Shift();
        shift.setPlatformOrganizationId(template.getPlatformOrganizationId());
        shift.setTemplateId(template.getId());
        shift.setLocationId(locationId);
        shift.setRoleId(template.getRoleId());
        shift.setName(template.getName());
        shift.setDescription(template.getDescription());
        shift.setAdditionalInstructions(template.getAdditionalInstructions());
        shift.setStartsAt(startsAt);
        shift.setEndsAt(endsAt);
        shift.setUnpaidBreakMinutes(template.getUnpaidBreakMinutes());
        shift.setWageType(template.getWageType());
        shift.setWageAmountMinor(template.getWageAmountMinor());
        shift.setCurrency(template.getCurrency());
        shift.setTipPercent(template.getTipPercent());
        shift.setTipAmountMinor(template.getTipAmountMinor());
        shift.setEmployeesNeeded(template.getEmployeesNeeded());
        shift.setDressCodeId(template.getDressCodeId());
        shift.setStaffSelectionType(template.getStaffSelectionType());
        shift.setStatus("OPEN");
        return shift;
    }

    private List<LocalDate> dates(ShiftTemplate template, GenerateShiftInstancesRequest request) {
        if (request != null && request.getDates() != null && !request.getDates().isEmpty()) {
            return request.getDates().stream().map(LocalDate::parse).toList();
        }
        if (request == null || request.getFrom() == null || request.getTo() == null) {
            throw StaffingRules.bad("dates or from/to is required");
        }
        LocalDate from = LocalDate.parse(request.getFrom());
        LocalDate to = LocalDate.parse(request.getTo());
        if (to.isBefore(from)) {
            throw StaffingRules.bad("to must be on or after from");
        }
        List<String> days = template.getDaysOfWeek() == null
                ? List.of()
                : java.util.Arrays.stream(template.getDaysOfWeek().toLowerCase(Locale.ROOT).split(",")).map(String::trim).toList();
        List<LocalDate> out = new ArrayList<>();
        for (LocalDate cursor = from; !cursor.isAfter(to); cursor = cursor.plusDays(1)) {
            String shortKey = switch (cursor.getDayOfWeek()) {
                case MONDAY -> "mon";
                case TUESDAY -> "tue";
                case WEDNESDAY -> "wed";
                case THURSDAY -> "thu";
                case FRIDAY -> "fri";
                case SATURDAY -> "sat";
                case SUNDAY -> "sun";
            };
            if (!template.isRecurring() || days.contains(shortKey)) {
                out.add(cursor);
            }
        }
        return out;
    }

    private ZoneId zone(Location location, UUID orgId) {
        if (location.getTimezone() != null && !location.getTimezone().isBlank()) {
            return ZoneId.of(location.getTimezone());
        }
        Organization org = organizations.findByPlatformOrganizationId(orgId)
                .orElseThrow(() -> StaffingRules.bad("organization profile does not exist"));
        return ZoneId.of(org.getTimezone());
    }

    private UUID resolveOrg(UUID locationId, CoreContext context, UUID requested) {
        Location location = locations.findById(locationId).filter(Location::live)
                .orElseThrow(() -> StaffingRules.bad("location does not exist"));
        UUID orgId = requested;
        if (location.getReferenceEntity() != null && location.getReferenceEntity().name().equals("ORGANIZATION")) {
            orgId = location.getReferenceId();
        }
        return StaffingScope.organizationId(context, orgId == null ? requested : orgId);
    }

    private List<ShiftRequirement> saveRequirements(String kind, UUID ownerId, List<ShiftRequirement> rows, CoreContext context) {
        List<ShiftRequirement> saved = new ArrayList<>();
        if (rows == null) {
            return saved;
        }
        for (ShiftRequirement row : rows) {
            row.setId(null);
            row.setOwnerKind(kind);
            row.setOwnerId(ownerId);
            row.markNew(StaffingScope.actor(context));
            saved.add(requirements.save(row));
        }
        return saved;
    }

    private List<ShiftRequirement> copyRequirements(List<ShiftRequirement> source, UUID instanceId, CoreContext context) {
        List<ShiftRequirement> copies = new ArrayList<>();
        if (source == null) {
            return copies;
        }
        for (ShiftRequirement row : source) {
            ShiftRequirement copy = new ShiftRequirement();
            copy.setOwnerKind("INSTANCE");
            copy.setOwnerId(instanceId);
            copy.setKind(row.getKind());
            copy.setSkillId(row.getSkillId());
            copy.setExpertiseLevel(row.getExpertiseLevel());
            copy.setExperienceDuration(row.getExperienceDuration());
            copy.setLanguage(row.getLanguage());
            copy.setLevel(row.getLevel());
            copy.setCertificationNeeded(row.getCertificationNeeded());
            copy.setCertificateId(row.getCertificateId());
            copy.setCustomName(row.getCustomName());
            copy.setDocumentId(row.getDocumentId());
            copy.setContactId(row.getContactId());
            copy.markNew(StaffingScope.actor(context));
            copies.add(requirements.save(copy));
        }
        return copies;
    }

    private void attach(ShiftTemplate row) {
        row.setRequirements(requirements.findByOwnerKindAndOwnerId(OWNER, row.getId()));
    }

    private ShiftTemplate live(UUID id) {
        return repository.findById(id).filter(ShiftTemplate::live).orElseThrow(() -> StaffingRules.notFound("Shift template not found"));
    }
}
