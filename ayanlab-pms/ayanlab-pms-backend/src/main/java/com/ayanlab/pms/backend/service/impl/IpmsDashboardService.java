package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.ApplicationOrganization;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ApplicationOrganizationRepository;
import com.ayanlab.core.repository.UserRepository;
import com.ayanlab.pms.backend.data.enums.*;
import com.ayanlab.pms.backend.data.responses.*;
import com.ayanlab.pms.backend.entity.associations.Violation;
import com.ayanlab.pms.backend.entity.leasing.Lease;
import com.ayanlab.pms.backend.entity.leasing.RentersInsurancePolicy;
import com.ayanlab.pms.backend.entity.maintenance.FieldStaffPresence;
import com.ayanlab.pms.backend.entity.maintenance.MaintenanceRequest;
import com.ayanlab.pms.backend.entity.maintenance.Vendor;
import com.ayanlab.pms.backend.entity.maintenance.WorkOrder;
import com.ayanlab.pms.backend.entity.onboarding.IpmsOnboardingProfile;
import com.ayanlab.pms.backend.entity.platform.IpmsActivityEvent;
import com.ayanlab.pms.backend.entity.rentals.OutstandingBalance;
import com.ayanlab.pms.backend.entity.rentals.Property;
import com.ayanlab.pms.backend.entity.rentals.PropertyUnit;
import com.ayanlab.pms.backend.repository.*;
import com.ayanlab.pms.backend.service.IIpmsDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpmsDashboardService implements IIpmsDashboardService {

    private final IpmsTransactionRepository transactionRepository;
    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyUnitRepository propertyUnitRepository;
    private final OutstandingBalanceRepository outstandingBalanceRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final VendorRepository vendorRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final UserRepository userRepository;
    private final IpmsOnboardingProfileRepository onboardingProfileRepository;
    private final RentersInsurancePolicyRepository rentersInsurancePolicyRepository;
    private final IpmsActivityEventRepository activityEventRepository;
    private final FieldStaffPresenceRepository fieldStaffPresenceRepository;
    private final ViolationRepository violationRepository;
    private final TaskRepository taskRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a");
    private static final ZoneId ZONE = ZoneId.systemDefault();

    @Override
    @Transactional(readOnly = true)
    public DashboardRevenueSummaryResponse revenueSummary(CoreContext context) {
        UUID orgId = requireOrgId(context);
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        long collected = transactionRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(t -> t.getType() == IpmsTransactionTypeEnum.INCOME)
                .filter(t -> t.getStatus() == IpmsTransactionStatusEnum.COMPLETED)
                .filter(t -> t.getTransactionDate() != null
                        && !t.getTransactionDate().isBefore(start)
                        && t.getTransactionDate().isBefore(end))
                .mapToLong(t -> t.getAmountCents() == null ? 0L : t.getAmountCents())
                .sum();

        long expected = leaseRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(l -> l.getStatus() == LeaseStatusEnum.ACTIVE)
                .mapToLong(l -> l.getMonthlyRentCents() == null ? 0L : l.getMonthlyRentCents())
                .sum();

        double percent = expected <= 0 ? 0.0 : Math.round((collected * 1000.0) / expected) / 10.0;
        return new DashboardRevenueSummaryResponse(collected, expected, percent);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardMyLeaseResponse myLease(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);

        List<Lease> leases = leaseRepository
                .findByOrganizationIdAndTenantProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, profileId);
        Lease lease = leases.stream()
                .filter(l -> l.getStatus() == LeaseStatusEnum.ACTIVE)
                .findFirst()
                .orElse(leases.isEmpty() ? null : leases.get(0));
        if (lease == null) {
            return null;
        }

        Property property = lease.getPropertyId() == null
                ? null
                : propertyRepository.findByIdAndOrganizationId(lease.getPropertyId(), orgId).orElse(null);
        PropertyUnit unit = lease.getUnitId() == null
                ? null
                : propertyUnitRepository.findByIdAndOrganizationId(lease.getUnitId(), orgId).orElse(null);

        String unitLabel;
        if (property != null && unit != null) {
            unitLabel = property.getName() + " - " + unit.getUnitNumber();
        } else if (property != null) {
            unitLabel = property.getName();
        } else if (unit != null) {
            unitLabel = "Unit " + unit.getUnitNumber();
        } else {
            unitLabel = "Lease";
        }

        return new DashboardMyLeaseResponse(
                lease.getId(),
                unitLabel,
                lease.getMonthlyRentCents() == null ? 0L : lease.getMonthlyRentCents(),
                lease.getEndDate(),
                null,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardRentPaymentResponse rentPayment(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);

        List<OutstandingBalance> balances = outstandingBalanceRepository
                .findByOrganizationIdAndTenantProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, profileId);

        long due = balances.stream()
                .filter(b -> b.getStatus() == OutstandingBalanceStatusEnum.OPEN
                        || b.getStatus() == OutstandingBalanceStatusEnum.PARTIAL)
                .mapToLong(b -> {
                    long amountDue = b.getAmountDueCents() == null ? 0L : b.getAmountDueCents();
                    long paid = b.getAmountPaidCents() == null ? 0L : b.getAmountPaidCents();
                    return Math.max(0L, amountDue - paid);
                })
                .sum();

        OutstandingBalance lastPaid = balances.stream()
                .filter(b -> b.getStatus() == OutstandingBalanceStatusEnum.PAID
                        || (b.getAmountPaidCents() != null && b.getAmountPaidCents() > 0))
                .max(Comparator.comparing(b -> b.getUpdatedAt() == null ? Instant.EPOCH : b.getUpdatedAt()))
                .orElse(null);

        Long lastAmount = lastPaid == null || lastPaid.getAmountPaidCents() == null
                ? null
                : lastPaid.getAmountPaidCents();
        Instant lastAt = lastPaid == null ? null : lastPaid.getUpdatedAt();

        return new DashboardRentPaymentResponse(due, lastAmount, lastAt, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardTenantMaintenanceItemResponse> myMaintenance(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);

        return maintenanceRequestRepository
                .findByOrganizationIdAndReportedByProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, profileId)
                .stream()
                .limit(10)
                .map(m -> new DashboardTenantMaintenanceItemResponse(
                        m.getId(),
                        m.getTitle(),
                        m.getStatus() == null ? null : m.getStatus().name(),
                        m.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardPlatformHealthResponse platformHealth(CoreContext context) {
        requireAppId(context);
        long activeOrgs = applicationOrganizationRepository.countByAppIdAndIsActiveTrue(context.getAppId());
        return new DashboardPlatformHealthResponse(
                "ok",
                activeOrgs,
                0L,
                "Live org count only — uptime/incidents not instrumented"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardPlatformOrgsResponse platformOrganizations(CoreContext context) {
        requireAppId(context);
        List<ApplicationOrganization> links =
                applicationOrganizationRepository.findByAppIdAndIsActiveTrue(context.getAppId());
        Set<UUID> orgIds = links.stream().map(ApplicationOrganization::getOrgId).collect(Collectors.toSet());

        Map<UUID, OrganizationCompanyTypeEnum> typeByOrg = onboardingProfileRepository.findByIsActiveTrue().stream()
                .filter(p -> p.getOrganizationId() != null && orgIds.contains(p.getOrganizationId()))
                .filter(p -> p.getCompanyType() != null)
                .collect(Collectors.toMap(
                        IpmsOnboardingProfile::getOrganizationId,
                        IpmsOnboardingProfile::getCompanyType,
                        (a, b) -> a
                ));

        long maintenance = 0;
        long propertyMgmt = 0;
        for (UUID orgId : orgIds) {
            OrganizationCompanyTypeEnum type = typeByOrg.get(orgId);
            if (type == OrganizationCompanyTypeEnum.MAINTENANCE || type == OrganizationCompanyTypeEnum.VENDOR) {
                maintenance++;
            } else {
                propertyMgmt++;
            }
        }

        return new DashboardPlatformOrgsResponse(
                orgIds.size(),
                List.of(
                        new DashboardPlatformOrgsResponse.OrgTypeCount("Property Management", propertyMgmt),
                        new DashboardPlatformOrgsResponse.OrgTypeCount("Maintenance", maintenance)
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardPlatformUsersResponse platformUsers(CoreContext context) {
        UUID appId = requireAppId(context);
        Instant startOfDay = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        long total = userRepository.countByAppIdAndIsActiveTrue(appId);
        long activeToday = userRepository.countByAppIdAndIsActiveTrueAndLastLoginAtGreaterThanEqual(appId, startOfDay);
        long newThisWeek = userRepository.countByAppIdAndIsActiveTrueAndCreatedAtGreaterThanEqual(appId, weekAgo);
        return new DashboardPlatformUsersResponse(total, activeToday, newThisWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardVendorWorkOrderResponse> vendorWorkOrders(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);

        List<Vendor> vendors = vendorRepository.findByOrganizationIdAndProfileIdAndIsActiveTrue(orgId, profileId);
        if (vendors.isEmpty()) {
            return List.of();
        }

        Map<UUID, Property> properties = propertyRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .collect(Collectors.toMap(Property::getId, p -> p, (a, b) -> a));

        List<DashboardVendorWorkOrderResponse> rows = new ArrayList<>();
        for (Vendor vendor : vendors) {
            List<WorkOrder> orders = workOrderRepository
                    .findByOrganizationIdAndVendorIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, vendor.getId());
            for (WorkOrder wo : orders) {
                if (wo.getStatus() == WorkOrderStatusEnum.COMPLETED
                        || wo.getStatus() == WorkOrderStatusEnum.CANCELLED) {
                    continue;
                }
                MaintenanceRequest request = wo.getMaintenanceRequestId() == null
                        ? null
                        : maintenanceRequestRepository
                                .findByIdAndOrganizationId(wo.getMaintenanceRequestId(), orgId)
                                .orElse(null);
                String issue = request != null && request.getTitle() != null
                        ? request.getTitle()
                        : (wo.getNotes() != null ? wo.getNotes() : "Work order");
                String propertyLabel = "—";
                if (request != null && request.getPropertyId() != null) {
                    Property p = properties.get(request.getPropertyId());
                    if (p != null) {
                        propertyLabel = p.getName();
                    }
                }
                String priority = request != null && request.getPriority() != null
                        ? request.getPriority().name()
                        : "MEDIUM";
                rows.add(new DashboardVendorWorkOrderResponse(
                        wo.getId(),
                        issue,
                        propertyLabel,
                        wo.getStatus() == null ? null : wo.getStatus().name(),
                        priority
                ));
            }
        }
        return rows.stream().limit(10).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardRentersInsuranceSummaryResponse rentersInsurance(CoreContext context) {
        UUID orgId = requireOrgId(context);
        List<RentersInsurancePolicy> policies = rentersInsurancePolicyRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId);
        LocalDate soon = LocalDate.now().plusDays(30);
        long insured = policies.stream()
                .filter(p -> p.getStatus() == RentersInsuranceStatusEnum.INSURED)
                .count();
        long uninsured = policies.stream()
                .filter(p -> p.getStatus() == RentersInsuranceStatusEnum.UNINSURED
                        || p.getStatus() == RentersInsuranceStatusEnum.EXPIRED
                        || p.getStatus() == RentersInsuranceStatusEnum.PENDING)
                .count();
        long expiringSoon = policies.stream()
                .filter(p -> p.getStatus() == RentersInsuranceStatusEnum.INSURED)
                .filter(p -> p.getExpirationDate() != null
                        && !p.getExpirationDate().isBefore(LocalDate.now())
                        && !p.getExpirationDate().isAfter(soon))
                .count();
        return new DashboardRentersInsuranceSummaryResponse(policies.size(), insured, uninsured, expiringSoon);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardTenantInsuranceResponse tenantInsurance(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);
        List<RentersInsurancePolicy> policies = rentersInsurancePolicyRepository
                .findByOrganizationIdAndTenantProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, profileId);
        RentersInsurancePolicy policy = policies.isEmpty() ? null : policies.get(0);
        if (policy == null) {
            return new DashboardTenantInsuranceResponse(
                    null,
                    RentersInsuranceStatusEnum.UNINSURED.name(),
                    true,
                    LocalDate.now().plusDays(14),
                    null,
                    null,
                    null
            );
        }
        return new DashboardTenantInsuranceResponse(
                policy.getId(),
                policy.getStatus() == null ? RentersInsuranceStatusEnum.UNINSURED.name() : policy.getStatus().name(),
                true,
                policy.getRequiredDeadline(),
                policy.getCarrierName(),
                policy.getPolicyNumber(),
                policy.getExpirationDate()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardActivityItemResponse> platformActivity(CoreContext context) {
        UUID appId = requireAppId(context);
        return activityEventRepository.findByAppIdAndIsActiveTrueOrderByOccurredAtDesc(appId).stream()
                .limit(20)
                .map(e -> new DashboardActivityItemResponse(
                        e.getId(),
                        e.getTitle(),
                        e.getDetail(),
                        e.getOccurredAt()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardVendorPerformanceResponse vendorPerformance(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);
        List<Vendor> vendors = vendorRepository.findByOrganizationIdAndProfileIdAndIsActiveTrue(orgId, profileId);
        List<WorkOrder> orders = new ArrayList<>();
        for (Vendor vendor : vendors) {
            orders.addAll(workOrderRepository
                    .findByOrganizationIdAndVendorIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, vendor.getId()));
        }
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        long completedWeek = orders.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatusEnum.COMPLETED)
                .filter(wo -> wo.getCompletedAt() != null && !wo.getCompletedAt().isBefore(weekAgo))
                .count();

        List<WorkOrder> withSla = orders.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatusEnum.COMPLETED)
                .filter(wo -> wo.getSlaDueAt() != null && wo.getCompletedAt() != null)
                .toList();
        double onTime = 0;
        if (!withSla.isEmpty()) {
            long onTimeCount = withSla.stream()
                    .filter(wo -> !wo.getCompletedAt().isAfter(wo.getSlaDueAt()))
                    .count();
            onTime = Math.round((onTimeCount * 1000.0) / withSla.size()) / 10.0;
        }

        List<WorkOrder> withResponse = orders.stream()
                .filter(wo -> wo.getRespondedAt() != null && wo.getCreatedAt() != null)
                .toList();
        double avgHrs = 0;
        if (!withResponse.isEmpty()) {
            double sumHrs = withResponse.stream()
                    .mapToDouble(wo -> Duration.between(wo.getCreatedAt(), wo.getRespondedAt()).toMinutes() / 60.0)
                    .sum();
            avgHrs = Math.round((sumHrs / withResponse.size()) * 10.0) / 10.0;
        }

        return new DashboardVendorPerformanceResponse(onTime, avgHrs, completedWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardVendorStaffItemResponse> vendorStaff(CoreContext context) {
        UUID orgId = requireOrgId(context);
        List<FieldStaffPresence> staff = fieldStaffPresenceRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId);
        Map<UUID, Long> openJobs = workOrderRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(wo -> wo.getAssignedToProfileId() != null)
                .filter(wo -> wo.getStatus() != WorkOrderStatusEnum.COMPLETED
                        && wo.getStatus() != WorkOrderStatusEnum.CANCELLED)
                .collect(Collectors.groupingBy(WorkOrder::getAssignedToProfileId, Collectors.counting()));

        return staff.stream()
                .map(s -> new DashboardVendorStaffItemResponse(
                        s.getProfileId(),
                        s.getDisplayName() != null ? s.getDisplayName() : "Staff",
                        formatPresence(s.getStatus()),
                        openJobs.getOrDefault(s.getProfileId(), 0L)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardStaffWorkOrderResponse> staffWorkOrders(CoreContext context) {
        return loadStaffOpenOrders(context).stream()
                .sorted(Comparator.comparing(wo -> wo.scheduledAt() == null ? Instant.MAX : wo.scheduledAt()))
                .limit(10)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStaffScheduleResponse staffSchedule(CoreContext context) {
        LocalDate today = LocalDate.now(ZONE);
        List<DashboardStaffScheduleResponse.ScheduleItem> items = loadStaffOpenOrders(context).stream()
                .filter(wo -> {
                    if (wo.scheduledAt() == null) return false;
                    return wo.scheduledAt().atZone(ZONE).toLocalDate().equals(today);
                })
                .sorted(Comparator.comparing(DashboardStaffWorkOrderResponse::scheduledAt))
                .map(wo -> new DashboardStaffScheduleResponse.ScheduleItem(
                        wo.timeLabel() != null ? wo.timeLabel() : "—",
                        wo.propertyLabel() + " — " + wo.issue(),
                        "work"
                ))
                .toList();
        return new DashboardStaffScheduleResponse(items);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStaffCompletedResponse staffCompleted(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<WorkOrder> done = workOrderRepository
                .findByOrganizationIdAndAssignedToProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, profileId)
                .stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatusEnum.COMPLETED)
                .filter(wo -> wo.getCompletedAt() != null && !wo.getCompletedAt().isBefore(weekAgo))
                .toList();
        double hours = done.stream()
                .map(WorkOrder::getHoursLogged)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        hours = BigDecimal.valueOf(hours).setScale(1, RoundingMode.HALF_UP).doubleValue();
        return new DashboardStaffCompletedResponse(done.size(), hours);
    }

    private List<DashboardStaffWorkOrderResponse> loadStaffOpenOrders(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID profileId = requireProfileId(context);
        Map<UUID, Property> properties = propertyRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .collect(Collectors.toMap(Property::getId, p -> p, (a, b) -> a));

        return workOrderRepository
                .findByOrganizationIdAndAssignedToProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, profileId)
                .stream()
                .filter(wo -> wo.getStatus() != WorkOrderStatusEnum.COMPLETED
                        && wo.getStatus() != WorkOrderStatusEnum.CANCELLED)
                .map(wo -> {
                    MaintenanceRequest request = wo.getMaintenanceRequestId() == null
                            ? null
                            : maintenanceRequestRepository
                                    .findByIdAndOrganizationId(wo.getMaintenanceRequestId(), orgId)
                                    .orElse(null);
                    String issue = request != null && request.getTitle() != null
                            ? request.getTitle()
                            : (wo.getNotes() != null ? wo.getNotes() : "Work order");
                    String propertyLabel = "—";
                    if (request != null && request.getPropertyId() != null) {
                        Property p = properties.get(request.getPropertyId());
                        if (p != null) propertyLabel = p.getName();
                    }
                    Instant scheduled = wo.getScheduledAt();
                    if (scheduled == null && wo.getScheduledDate() != null) {
                        scheduled = wo.getScheduledDate().atStartOfDay(ZONE).toInstant();
                    }
                    String timeLabel = scheduled == null
                            ? null
                            : TIME_FMT.format(scheduled.atZone(ZONE));
                    return new DashboardStaffWorkOrderResponse(
                            wo.getId(),
                            issue,
                            propertyLabel,
                            wo.getStatus() == null ? null : wo.getStatus().name(),
                            scheduled,
                            timeLabel
                    );
                })
                .toList();
    }

    private String formatPresence(FieldStaffPresenceStatusEnum status) {
        if (status == null) return "Available";
        return switch (status) {
            case ON_SITE -> "On site";
            case EN_ROUTE -> "En route";
            case OFF_DUTY -> "Off duty";
            case AVAILABLE -> "Available";
        };
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardPmWidgetsResponse pmWidgets(CoreContext context) {
        UUID orgId = requireOrgId(context);
        UUID appId = context.getAppId();
        LocalDate today = LocalDate.now();

        Map<UUID, Property> properties = propertyRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .collect(Collectors.toMap(Property::getId, p -> p, (a, b) -> a));

        List<DashboardPmWidgetsResponse.BalanceWidget> balances = outstandingBalanceRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(b -> b.getStatus() == OutstandingBalanceStatusEnum.OPEN
                        || b.getStatus() == OutstandingBalanceStatusEnum.PARTIAL)
                .limit(8)
                .map(b -> {
                    long due = Math.max(0L, (b.getAmountDueCents() == null ? 0L : b.getAmountDueCents())
                            - (b.getAmountPaidCents() == null ? 0L : b.getAmountPaidCents()));
                    int days = b.getDueDate() == null
                            ? 0
                            : (int) ChronoUnit.DAYS.between(b.getDueDate(), today);
                    if (days < 0) {
                        days = 0;
                    }
                    String party = b.getDescription() != null && !b.getDescription().isBlank()
                            ? b.getDescription()
                            : "Resident";
                    return new DashboardPmWidgetsResponse.BalanceWidget(
                            b.getId(),
                            "—",
                            party,
                            "TENANT",
                            due,
                            days
                    );
                })
                .toList();

        List<DashboardPmWidgetsResponse.MaintenanceWidget> maintenance = maintenanceRequestRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(m -> m.getStatus() != MaintenanceStatusEnum.COMPLETED
                        && m.getStatus() != MaintenanceStatusEnum.CANCELLED)
                .limit(6)
                .map(m -> {
                    String propName = "—";
                    if (m.getPropertyId() != null) {
                        Property p = properties.get(m.getPropertyId());
                        if (p != null && p.getName() != null) {
                            propName = p.getName();
                        }
                    }
                    String status = m.getStatus() == MaintenanceStatusEnum.OPEN ? "OPEN" : "OPEN";
                    return new DashboardPmWidgetsResponse.MaintenanceWidget(
                            m.getId(),
                            propName,
                            m.getTitle(),
                            status,
                            m.getCreatedAt()
                    );
                })
                .toList();

        List<DashboardPmWidgetsResponse.ActivityWidget> activity = (appId == null
                ? List.<IpmsActivityEvent>of()
                : activityEventRepository.findByAppIdAndOrganizationIdAndIsActiveTrueOrderByOccurredAtDesc(appId, orgId))
                .stream()
                .limit(6)
                .map(e -> new DashboardPmWidgetsResponse.ActivityWidget(
                        e.getId(),
                        e.getTitle(),
                        e.getOccurredAt() != null ? e.getOccurredAt() : e.getCreatedAt()
                ))
                .toList();

        List<DashboardPmWidgetsResponse.InsuranceGapWidget> insuranceGaps = rentersInsurancePolicyRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(p -> p.getStatus() == RentersInsuranceStatusEnum.UNINSURED
                        || p.getStatus() == RentersInsuranceStatusEnum.EXPIRED)
                .limit(6)
                .map(p -> new DashboardPmWidgetsResponse.InsuranceGapWidget(
                        "Resident",
                        p.getStatus() == RentersInsuranceStatusEnum.EXPIRED ? "EXPIRED" : "MISSING"
                ))
                .toList();

        LocalDate expiringBefore = today.plusDays(60);
        List<DashboardPmWidgetsResponse.ExpiringLeaseWidget> expiringLeases = leaseRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(l -> l.getStatus() == LeaseStatusEnum.ACTIVE)
                .filter(l -> l.getEndDate() != null && !l.getEndDate().isAfter(expiringBefore))
                .limit(8)
                .map(l -> new DashboardPmWidgetsResponse.ExpiringLeaseWidget(
                        l.getId(),
                        "EXPIRING",
                        l.getEndDate().toString()
                ))
                .toList();

        long vacant = propertyUnitRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(u -> u.getStatus() == UnitStatusEnum.VACANT)
                .count();

        List<DashboardPmWidgetsResponse.ViolationWidget> violations = violationRepository
                .findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(v -> v.getStatus() == ViolationStatusEnum.OPEN
                        || v.getStatus() == ViolationStatusEnum.UNDER_REVIEW)
                .limit(8)
                .map(this::toViolationWidget)
                .toList();

        long delinquent = violations.stream()
                .mapToLong(DashboardPmWidgetsResponse.ViolationWidget::fineCents)
                .sum();

        int openTasks = (int) taskRepository.findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId)
                .stream()
                .filter(t -> t.getStatus() == TaskStatusEnum.TODO || t.getStatus() == TaskStatusEnum.IN_PROGRESS)
                .count();

        return new DashboardPmWidgetsResponse(
                balances,
                maintenance,
                activity,
                insuranceGaps,
                expiringLeases,
                new DashboardPmWidgetsResponse.ListingStatsWidget((int) vacant, 0),
                violations,
                delinquent,
                List.of(),
                openTasks
        );
    }

    private DashboardPmWidgetsResponse.ViolationWidget toViolationWidget(Violation v) {
        String uiStatus = switch (v.getStatus() == null ? ViolationStatusEnum.OPEN : v.getStatus()) {
            case OPEN -> "OPEN";
            case UNDER_REVIEW -> "HEARING";
            case RESOLVED, DISMISSED -> "CLOSED";
        };
        return new DashboardPmWidgetsResponse.ViolationWidget(
                v.getId(),
                v.getAssociationId(),
                "—",
                uiStatus,
                v.getFineAmountCents() == null ? 0L : v.getFineAmountCents(),
                "General",
                v.getDescription() != null ? v.getDescription() : v.getTitle(),
                v.getCreatedAt()
        );
    }

    private UUID requireOrgId(CoreContext context) {
        if (context.getOrgId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "orgId required in context");
        }
        return context.getOrgId();
    }

    private UUID requireProfileId(CoreContext context) {
        if (context.getProfileId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "profileId required in context");
        }
        return context.getProfileId();
    }

    private UUID requireAppId(CoreContext context) {
        if (context.getAppId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required in context");
        }
        return context.getAppId();
    }
}
