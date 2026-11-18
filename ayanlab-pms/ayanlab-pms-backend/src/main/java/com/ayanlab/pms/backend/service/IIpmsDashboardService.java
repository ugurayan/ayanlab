package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.pms.backend.data.responses.*;

import java.util.List;

public interface IIpmsDashboardService {

    DashboardRevenueSummaryResponse revenueSummary(CoreContext context);

    DashboardMyLeaseResponse myLease(CoreContext context);

    DashboardRentPaymentResponse rentPayment(CoreContext context);

    List<DashboardTenantMaintenanceItemResponse> myMaintenance(CoreContext context);

    DashboardPlatformHealthResponse platformHealth(CoreContext context);

    DashboardPlatformOrgsResponse platformOrganizations(CoreContext context);

    DashboardPlatformUsersResponse platformUsers(CoreContext context);

    List<DashboardVendorWorkOrderResponse> vendorWorkOrders(CoreContext context);

    DashboardRentersInsuranceSummaryResponse rentersInsurance(CoreContext context);

    DashboardTenantInsuranceResponse tenantInsurance(CoreContext context);

    List<DashboardActivityItemResponse> platformActivity(CoreContext context);

    DashboardVendorPerformanceResponse vendorPerformance(CoreContext context);

    List<DashboardVendorStaffItemResponse> vendorStaff(CoreContext context);

    List<DashboardStaffWorkOrderResponse> staffWorkOrders(CoreContext context);

    DashboardStaffScheduleResponse staffSchedule(CoreContext context);

    DashboardStaffCompletedResponse staffCompleted(CoreContext context);
}
