package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.config.HrmDataProperties;
import com.ayanlab.hrm.backend.entity.Application;
import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.util.Ids;
import com.ayanlab.hrm.backend.entity.Worker;
import com.ayanlab.hrm.backend.entity.WorkerEarnings;
import com.ayanlab.hrm.backend.entity.WorkerIncident;
import com.ayanlab.hrm.backend.repository.ApplicationRepository;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.repository.WorkerIncidentRepository;
import com.ayanlab.hrm.backend.repository.WorkerRepository;
import com.ayanlab.hrm.backend.service.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MockDataServiceImpl implements MockDataService {

    private final HrmDataProperties dataProperties;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final EntityManager entityManager;
    private final OrganizationRepository organizationRepository;
    private final LocationRepository locationRepository;
    private final ShiftRepository shiftRepository;
    private final ApplicationRepository applicationRepository;
    private final WorkerRepository workerRepository;
    private final WorkerIncidentRepository workerIncidentRepository;
    private final MockDataSeeder mockDataSeeder;

    @Override
    @Transactional
    public void reloadFromClasspathResource() throws Exception {
        Resource resource = resourceLoader.getResource(dataProperties.getMockDataResource());
        if (!resource.exists()) {
            throw new IllegalStateException("Missing mock data: " + dataProperties.getMockDataResource());
        }
        truncateAll();
        try (InputStream in = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            loadOrganizations(root.path("orgs"));
            loadLocations(root.path("locations"));
            loadShifts(root.path("shifts"));
            loadApplications(root.path("applications"));
            loadWorkers(root.path("workers"));
            mockDataSeeder.seedFromRoot(root);
        }
    }

    private void truncateAll() {
        entityManager.createNativeQuery("DELETE FROM \"WorkerSkill\"").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM \"WorkerSkillTag\"").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM \"WorkerJobProfile\"").executeUpdate();
        workerIncidentRepository.deleteAllInBatch();
        applicationRepository.deleteAllInBatch();
        workerRepository.deleteAllInBatch();
        entityManager.createNativeQuery("DELETE FROM \"ShiftRequirement\"").executeUpdate();
        shiftRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();
        mockDataSeeder.truncateCrudTables();
        entityManager.flush();
    }

    private void loadOrganizations(JsonNode nodes) {
        List<Organization> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            Organization row = new Organization();
            UUID id = Ids.parse(text(n, "id"));
            row.setId(id);
            row.setPlatformOrganizationId(id);
            row.setName(text(n, "name"));
            String email = textOrNull(n, "email");
            row.setEmail(email != null ? email : text(n, "id") + "@example.com");
            row.setPhone("+41000000000");
            String country = text(n, "country");
            row.setTimezone("DE".equals(country) ? "Europe/Berlin" : "Europe/Zurich");
            row.setCurrency("DE".equals(country) ? "EUR" : "CHF");
            row.setIsActive(true);
            row.setIsDeleted(false);
            row.setCreatedAt(instantOrNow(n, "createdAt"));
            row.setUpdatedAt(row.getCreatedAt());
            rows.add(row);
        }
        organizationRepository.saveAll(rows);
    }

    private void loadLocations(JsonNode nodes) {
        List<Location> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            Location row = new Location();
            row.setId(Ids.parse(text(n, "id")));
            row.setReferenceEntity(ReferenceEntityKind.ORGANIZATION);
            row.setReferenceId(Ids.parse(text(n, "orgId")));
            row.setName(text(n, "name"));
            row.setCity(textOrNull(n, "city"));
            row.setFormattedAddress(text(n, "name") + ", " + text(n, "city"));
            row.setIsActive(true);
            row.setIsDeleted(false);
            row.setCreatedAt(instantOrNow(n, "createdAt"));
            row.setUpdatedAt(row.getCreatedAt());
            rows.add(row);
        }
        locationRepository.saveAll(rows);
    }

    private void loadShifts(JsonNode nodes) {
        List<Shift> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            Shift row = new Shift();
            row.setId(Ids.parse(text(n, "id")));
            row.setPlatformOrganizationId(Ids.parse(text(n, "orgId")));
            row.setLocationId(Ids.parse(text(n, "locationId")));
            row.setRoleId(Ids.parse(text(n, "role")));
            row.setName(text(n, "role"));
            row.setDescription(text(n, "role"));
            Instant start = instantOrNow(n, "startAt");
            Instant end = instantOrNow(n, "endAt");
            if (!end.isAfter(start)) {
                end = start.plusSeconds(8 * 3600);
            }
            row.setStartsAt(start);
            row.setEndsAt(end);
            row.setUnpaidBreakMinutes(0);
            row.setWageType("HOURLY");
            row.setWageAmountMinor(2500);
            row.setCurrency("CHF");
            row.setEmployeesNeeded(Math.max(1, n.path("headcount").asInt(1)));
            row.setStaffSelectionType("smart-assign");
            row.setStatus(mapStatus(text(n, "status")));
            row.setIsActive(true);
            row.setIsDeleted(false);
            row.setCreatedAt(start);
            row.setUpdatedAt(start);
            rows.add(row);
        }
        shiftRepository.saveAll(rows);
    }

    private void loadApplications(JsonNode nodes) {
        List<Application> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            Application row = new Application();
            row.setId(Ids.parse(text(n, "id")));
            row.setShiftId(Ids.parse(text(n, "shiftId")));
            row.setWorkerId(Ids.parse(text(n, "workerId")));
            row.setStatus(textOrNull(n, "status"));
            rows.add(row);
        }
        applicationRepository.saveAll(rows);
    }

    private void loadWorkers(JsonNode nodes) {
        List<Worker> workers = new ArrayList<>();
        List<WorkerIncident> incidents = new ArrayList<>();
        for (JsonNode n : nodes) {
            Worker w = new Worker();
            w.setId(Ids.parse(text(n, "id")));
            w.setDisplayName(text(n, "displayName"));
            w.setRating(n.path("rating").asDouble());
            w.setRatingCount(n.path("ratingCount").asInt());
            w.setReliability(n.path("reliability").asDouble());
            w.setAcceptanceRate(n.path("acceptanceRate").asDouble());
            w.setOnTimeStarts(n.path("onTimeStarts").asInt());
            w.setLateStarts(n.path("lateStarts").asInt());
            w.setJobsCompleted(n.path("jobsCompleted").asInt());
            w.setJobsNotCompleted(n.path("jobsNotCompleted").asInt());
            w.setNoShows(n.path("noShows").asInt());
            w.setCriticalIncidents(n.path("criticalIncidents").asInt());
            w.setReliabilityIncidents(n.path("reliabilityIncidents").asInt());
            w.setAvgHourlyRateCents(n.path("avgHourlyRateCents").asLong());
            w.setNotes(textOrNull(n, "notes"));

            JsonNode earnings = n.path("earnings");
            if (!earnings.isMissingNode()) {
                w.setEarnings(objectMapper.convertValue(earnings, WorkerEarnings.class));
            }

            List<String> profiles = new ArrayList<>();
            for (JsonNode p : n.path("jobProfiles")) {
                profiles.add(p.asText());
            }
            w.setJobProfiles(profiles);

            List<String> skills = new ArrayList<>();
            for (JsonNode s : n.path("skills")) {
                skills.add(s.asText());
            }
            w.setSkills(skills);

            workers.add(w);

            for (JsonNode inc : n.path("lastIncidents")) {
                WorkerIncident incident = new WorkerIncident();
                incident.setId(Ids.parse(text(inc, "id")));
                incident.setWorkerId(Ids.parse(text(inc, "workerId")));
                incident.setType(textOrNull(inc, "type"));
                incident.setSeverity(textOrNull(inc, "severity"));
                incident.setOccurredAt(textOrNull(inc, "occurredAt"));
                incident.setSummary(textOrNull(inc, "summary"));
                incidents.add(incident);
            }
        }
        workerRepository.saveAll(workers);
        workerIncidentRepository.saveAll(incidents);
    }

    private static String mapStatus(String status) {
        return switch (status == null ? "" : status) {
            case "FILLED" -> "FILLED";
            case "COMPLETED" -> "COMPLETED";
            case "CANCELLED" -> "CANCELLED";
            case "IN_PROGRESS" -> "IN_PROGRESS";
            default -> "OPEN";
        };
    }

    private static Instant instantOrNow(JsonNode n, String field) {
        String raw = textOrNull(n, field);
        if (raw == null) {
            return Instant.now();
        }
        try {
            return Instant.parse(raw);
        } catch (Exception ex) {
            return Instant.now();
        }
    }

    private static String text(JsonNode n, String field) {
        return n.path(field).asText();
    }

    private static String textOrNull(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asText();
    }
}