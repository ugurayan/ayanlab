package com.ayanlab.pms.backend.platform.startup;

import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.pms.backend.data.enums.*;
import com.ayanlab.pms.backend.entity.associations.Association;
import com.ayanlab.pms.backend.entity.rentals.Property;
import com.ayanlab.pms.backend.entity.rentals.PropertyType;
import com.ayanlab.pms.backend.entity.tr.*;
import com.ayanlab.pms.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Optional demo domain rows (rental + HOA + TR modules) when {@code ipms.demo.organization-id} is set.
 * Org must exist on platform (e.g. {@code demo_ayanlab} from {@code db/authz/ipms.json}).
 */
@Component
@Order(17)
public class IpmsDemoDataLoader implements CommandLineRunner {

    private final PropertyRepository propertyRepository;
    private final AssociationRepository associationRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final LatePenaltyRuleRepository latePenaltyRuleRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final DebtInquiryCodeRepository debtInquiryCodeRepository;
    private final ParcelDeliveryRepository parcelDeliveryRepository;
    private final EnforcementCaseRepository enforcementCaseRepository;
    private final PatrolRouteRepository patrolRouteRepository;
    private final PatrolCheckpointRepository patrolCheckpointRepository;
    private final AccessControlEventRepository accessControlEventRepository;
    private final StaffTimesheetRepository staffTimesheetRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ipms.demo.enabled:false}")
    private boolean enabled;

    @Value("${ipms.demo.organization-id:}")
    private String organizationIdText;

    public IpmsDemoDataLoader(
            PropertyRepository propertyRepository,
            AssociationRepository associationRepository,
            PropertyTypeRepository propertyTypeRepository,
            LatePenaltyRuleRepository latePenaltyRuleRepository,
            MeterReadingRepository meterReadingRepository,
            DebtInquiryCodeRepository debtInquiryCodeRepository,
            ParcelDeliveryRepository parcelDeliveryRepository,
            EnforcementCaseRepository enforcementCaseRepository,
            PatrolRouteRepository patrolRouteRepository,
            PatrolCheckpointRepository patrolCheckpointRepository,
            AccessControlEventRepository accessControlEventRepository,
            StaffTimesheetRepository staffTimesheetRepository) {
        this.propertyRepository = propertyRepository;
        this.associationRepository = associationRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        this.latePenaltyRuleRepository = latePenaltyRuleRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.debtInquiryCodeRepository = debtInquiryCodeRepository;
        this.parcelDeliveryRepository = parcelDeliveryRepository;
        this.enforcementCaseRepository = enforcementCaseRepository;
        this.patrolRouteRepository = patrolRouteRepository;
        this.patrolCheckpointRepository = patrolCheckpointRepository;
        this.accessControlEventRepository = accessControlEventRepository;
        this.staffTimesheetRepository = staffTimesheetRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!enabled) {
            System.out.println("[IpmsDemoDataLoader] Disabled (ipms.demo.enabled=false)");
            return;
        }
        if (organizationIdText == null || organizationIdText.isBlank()) {
            System.out.println("[IpmsDemoDataLoader] Skipped: set ipms.demo.organization-id to platform org UUID");
            return;
        }
        UUID orgId = UUID.fromString(organizationIdText.trim());

        ClassPathResource res = new ClassPathResource("db/ipms-demo-seed.json");
        if (!res.exists()) {
            return;
        }

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            if (!propertyRepository.findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId).isEmpty()) {
                System.out.println("[IpmsDemoDataLoader] Skipped: org already has properties");
                return;
            }

            PropertyType type = propertyTypeRepository.findByOrganizationIdIsNullAndCode("multi_family")
                    .or(() -> propertyTypeRepository.findAll().stream().findFirst())
                    .orElse(null);
            if (type == null) {
                System.out.println("[IpmsDemoDataLoader] Skipped: no property type catalog");
                return;
            }

            JsonNode p = root.get("property");
            Property property = Property.builder()
                    .organizationId(orgId)
                    .name(p.get("name").asText())
                    .propertyTypeId(type.getId())
                    .addressLine1(p.get("addressLine1").asText())
                    .city(p.get("city").asText())
                    .state(p.get("state").asText())
                    .zipCode(p.get("zipCode").asText())
                    .country(p.has("country") ? p.get("country").asText() : "TR")
                    .status(PropertyStatusEnum.ACTIVE)
                    .build();
            stamp(property);
            propertyRepository.save(property);

            JsonNode a = root.get("association");
            Association association = Association.builder()
                    .organizationId(orgId)
                    .code(a.get("code").asText())
                    .name(a.get("name").asText())
                    .associationType(AssociationTypeEnum.valueOf(a.get("associationType").asText()))
                    .city(a.has("city") ? a.get("city").asText() : null)
                    .build();
            stamp(association);
            associationRepository.save(association);

            JsonNode tr = root.get("tr");
            if (tr != null && !tr.isNull()) {
                seedTr(orgId, tr);
            }

            System.out.println("[IpmsDemoDataLoader] Seeded property, association, and TR demo for org " + orgId);
        }
    }

    private void seedTr(UUID orgId, JsonNode tr) {
        JsonNode rule = tr.get("latePenaltyRule");
        if (rule != null && !rule.isNull()) {
            LatePenaltyRule entity = LatePenaltyRule.builder()
                    .organizationId(orgId)
                    .name(rule.get("name").asText())
                    .rateBps(rule.get("rateBps").asInt())
                    .graceDays(rule.get("graceDays").asInt())
                    .compound(rule.get("compound").asBoolean())
                    .build();
            stamp(entity);
            latePenaltyRuleRepository.save(entity);
        }

        JsonNode meters = tr.get("meterReadings");
        if (meters != null && meters.isArray()) {
            for (JsonNode row : meters) {
                MeterReading entity = MeterReading.builder()
                        .organizationId(orgId)
                        .unitLabel(row.get("unitLabel").asText())
                        .meterType(MeterTypeEnum.valueOf(row.get("meterType").asText()))
                        .previousValue(BigDecimal.valueOf(row.get("previousValue").asDouble()))
                        .currentValue(BigDecimal.valueOf(row.get("currentValue").asDouble()))
                        .readAt(Instant.now())
                        .build();
                stamp(entity);
                meterReadingRepository.save(entity);
            }
        }

        JsonNode codes = tr.get("debtInquiryCodes");
        if (codes != null && codes.isArray()) {
            for (JsonNode row : codes) {
                DebtInquiryCode entity = DebtInquiryCode.builder()
                        .organizationId(orgId)
                        .partyName(row.get("partyName").asText())
                        .code10(row.get("code10").asText())
                        .unitLabel(row.get("unitLabel").asText())
                        .expiresAt(Instant.now().plusSeconds(86400L * 30))
                        .build();
                stamp(entity);
                debtInquiryCodeRepository.save(entity);
            }
        }

        JsonNode parcels = tr.get("parcelDeliveries");
        if (parcels != null && parcels.isArray()) {
            for (JsonNode row : parcels) {
                ParcelDelivery entity = ParcelDelivery.builder()
                        .organizationId(orgId)
                        .carrier(row.get("carrier").asText())
                        .trackingNumber(row.path("trackingNumber").asText(null))
                        .lockerCode(row.path("lockerCode").asText(null))
                        .unitLabel(row.get("unitLabel").asText())
                        .status(ParcelDeliveryStatusEnum.valueOf(row.get("status").asText()))
                        .build();
                stamp(entity);
                parcelDeliveryRepository.save(entity);
            }
        }

        JsonNode cases = tr.get("enforcementCases");
        if (cases != null && cases.isArray()) {
            for (JsonNode row : cases) {
                EnforcementCase entity = EnforcementCase.builder()
                        .organizationId(orgId)
                        .ownerName(row.get("ownerName").asText())
                        .unitLabel(row.get("unitLabel").asText())
                        .amountCents(row.get("amountCents").asLong())
                        .status(EnforcementCaseStatusEnum.valueOf(row.get("status").asText()))
                        .openedAt(Instant.now())
                        .timelineJson(row.path("timelineJson").asText(null))
                        .build();
                stamp(entity);
                enforcementCaseRepository.save(entity);
            }
        }

        JsonNode route = tr.get("patrolRoute");
        if (route != null && !route.isNull()) {
            PatrolRoute patrolRoute = PatrolRoute.builder()
                    .organizationId(orgId)
                    .name(route.get("name").asText())
                    .active(true)
                    .build();
            stamp(patrolRoute);
            patrolRouteRepository.save(patrolRoute);

            JsonNode checkpoints = route.get("checkpoints");
            if (checkpoints != null && checkpoints.isArray()) {
                for (JsonNode cp : checkpoints) {
                    PatrolCheckpoint checkpoint = PatrolCheckpoint.builder()
                            .organizationId(orgId)
                            .routeId(patrolRoute.getId())
                            .name(cp.get("name").asText())
                            .sortOrder(cp.get("sortOrder").asInt())
                            .build();
                    stamp(checkpoint);
                    patrolCheckpointRepository.save(checkpoint);
                }
            }
        }

        JsonNode events = tr.get("accessControlEvents");
        if (events != null && events.isArray()) {
            Instant now = Instant.now();
            for (JsonNode row : events) {
                AccessControlEvent entity = AccessControlEvent.builder()
                        .organizationId(orgId)
                        .plateOrCard(row.get("plateOrCard").asText())
                        .eventType(AccessControlEventTypeEnum.valueOf(row.get("eventType").asText()))
                        .occurredAt(now)
                        .gateName(row.path("gateName").asText(null))
                        .build();
                stamp(entity);
                accessControlEventRepository.save(entity);
            }
        }

        JsonNode timesheets = tr.get("staffTimesheets");
        if (timesheets != null && timesheets.isArray()) {
            for (JsonNode row : timesheets) {
                StaffTimesheet entity = StaffTimesheet.builder()
                        .organizationId(orgId)
                        .staffName(row.get("staffName").asText())
                        .workDate(LocalDate.parse(row.get("workDate").asText()))
                        .hoursWorked(BigDecimal.valueOf(row.get("hoursWorked").asDouble()))
                        .build();
                stamp(entity);
                staffTimesheetRepository.save(entity);
            }
        }
    }

    private void stamp(com.ayanlab.core.entity.BaseEntity entity) {
        entity.setIsActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        IdGenerator.assignIfAbsent(entity);
    }
}