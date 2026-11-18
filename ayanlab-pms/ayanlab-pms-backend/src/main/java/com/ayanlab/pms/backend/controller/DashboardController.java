package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.data.responses.*;
import com.ayanlab.pms.backend.service.IIpmsDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/pms/api/dashboard")
@Tag(name = "IPMS · Dashboard", description = "Role dashboard summary endpoints (Phase B)")
@RequiredArgsConstructor
public class DashboardController extends BaseController {

    private static final String BASE_PATH = "/pms/api/dashboard";

    private final IIpmsDashboardService dashboardService;

    @GetMapping(value = "/pm/revenue-summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardRevenueSummaryResponse>> revenueSummary(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-pm-revenue", "GET", BASE_PATH + "/pm/revenue-summary", headers, null, null);
        try {
            DashboardRevenueSummaryResponse data = dashboardService.revenueSummary(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/tenant/my-lease", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardMyLeaseResponse>> myLease(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-tenant-my-lease", "GET", BASE_PATH + "/tenant/my-lease", headers, null, null);
        try {
            DashboardMyLeaseResponse data = dashboardService.myLease(call.context());
            call.trace().success(Map.of("found", data != null, "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/tenant/rent-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardRentPaymentResponse>> rentPayment(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-tenant-rent", "GET", BASE_PATH + "/tenant/rent-payment", headers, null, null);
        try {
            DashboardRentPaymentResponse data = dashboardService.rentPayment(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/tenant/maintenance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DashboardTenantMaintenanceItemResponse>>> myMaintenance(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-tenant-maintenance", "GET", BASE_PATH + "/tenant/maintenance", headers, null, null);
        try {
            List<DashboardTenantMaintenanceItemResponse> data = dashboardService.myMaintenance(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/platform/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardPlatformHealthResponse>> platformHealth(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-platform-health", "GET", BASE_PATH + "/platform/health", headers, null, null);
        try {
            DashboardPlatformHealthResponse data = dashboardService.platformHealth(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/platform/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardPlatformOrgsResponse>> platformOrganizations(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-platform-orgs", "GET", BASE_PATH + "/platform/organizations", headers, null, null);
        try {
            DashboardPlatformOrgsResponse data = dashboardService.platformOrganizations(call.context());
            call.trace().success(Map.of("total", data.total(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/platform/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardPlatformUsersResponse>> platformUsers(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-platform-users", "GET", BASE_PATH + "/platform/users", headers, null, null);
        try {
            DashboardPlatformUsersResponse data = dashboardService.platformUsers(call.context());
            call.trace().success(Map.of("total", data.total(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/vendor/work-orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DashboardVendorWorkOrderResponse>>> vendorWorkOrders(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-vendor-work-orders", "GET", BASE_PATH + "/vendor/work-orders", headers, null, null);
        try {
            List<DashboardVendorWorkOrderResponse> data = dashboardService.vendorWorkOrders(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/pm/renters-insurance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardRentersInsuranceSummaryResponse>> rentersInsurance(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-pm-renters-insurance", "GET", BASE_PATH + "/pm/renters-insurance", headers, null, null);
        try {
            DashboardRentersInsuranceSummaryResponse data = dashboardService.rentersInsurance(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/tenant/insurance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardTenantInsuranceResponse>> tenantInsurance(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-tenant-insurance", "GET", BASE_PATH + "/tenant/insurance", headers, null, null);
        try {
            DashboardTenantInsuranceResponse data = dashboardService.tenantInsurance(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/platform/activity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DashboardActivityItemResponse>>> platformActivity(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-platform-activity", "GET", BASE_PATH + "/platform/activity", headers, null, null);
        try {
            List<DashboardActivityItemResponse> data = dashboardService.platformActivity(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/vendor/performance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardVendorPerformanceResponse>> vendorPerformance(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-vendor-performance", "GET", BASE_PATH + "/vendor/performance", headers, null, null);
        try {
            DashboardVendorPerformanceResponse data = dashboardService.vendorPerformance(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/vendor/staff", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DashboardVendorStaffItemResponse>>> vendorStaff(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-vendor-staff", "GET", BASE_PATH + "/vendor/staff", headers, null, null);
        try {
            List<DashboardVendorStaffItemResponse> data = dashboardService.vendorStaff(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/staff/work-orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DashboardStaffWorkOrderResponse>>> staffWorkOrders(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-staff-work-orders", "GET", BASE_PATH + "/staff/work-orders", headers, null, null);
        try {
            List<DashboardStaffWorkOrderResponse> data = dashboardService.staffWorkOrders(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/staff/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardStaffScheduleResponse>> staffSchedule(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-staff-schedule", "GET", BASE_PATH + "/staff/schedule", headers, null, null);
        try {
            DashboardStaffScheduleResponse data = dashboardService.staffSchedule(call.context());
            call.trace().success(Map.of("count", data.items().size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/staff/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardStaffCompletedResponse>> staffCompleted(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "dashboard-staff-completed", "GET", BASE_PATH + "/staff/completed", headers, null, null);
        try {
            DashboardStaffCompletedResponse data = dashboardService.staffCompleted(call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
