package com.ayanlab.pms.backend.security;

import java.util.Map;

/**
 * Maps IPMS controller {@code operationPrefix} values to AuthZ catalog resource codes ({@code ipms.json}).
 */
public final class IpmsAuthzResources {

    private IpmsAuthzResources() {}

    private static final Map<String, String> PREFIX_TO_RESOURCE = Map.ofEntries(
            Map.entry("properties", "properties"),
            Map.entry("property-units", "properties"),
            Map.entry("tenants", "tenants"),
            Map.entry("rental-owners", "rental_owners"),
            Map.entry("applicants", "applicants"),
            Map.entry("leases", "leasing"),
            Map.entry("listings", "leasing"),
            Map.entry("draft-leases", "leasing"),
            Map.entry("lease-renewals", "leasing"),
            Map.entry("renters-insurance", "leasing"),
            Map.entry("associations", "associations"),
            Map.entry("violations", "violations"),
            Map.entry("architectural-requests", "architectural_requests"),
            Map.entry("ownership-accounts", "occupants"),
            Map.entry("transactions", "accounting"),
            Map.entry("recurring-transactions", "accounting"),
            Map.entry("financial-records", "accounting"),
            Map.entry("budgets", "accounting"),
            Map.entry("bills", "accounting"),
            Map.entry("bank-accounts", "accounting"),
            Map.entry("chart-of-accounts", "accounting"),
            Map.entry("eft-approvals", "accounting"),
            Map.entry("outstanding-balances", "accounting"),
            Map.entry("tax-filings-1099", "accounting"),
            Map.entry("maintenance-requests", "maintenance"),
            Map.entry("work-orders", "work_orders"),
            Map.entry("vendors", "vendors"),
            Map.entry("property-inspections", "maintenance"),
            Map.entry("field-staff-presence", "maintenance"),
            Map.entry("tasks", "tasks"),
            Map.entry("recurring-tasks", "tasks"),
            Map.entry("incoming-requests", "tasks"),
            Map.entry("announcements", "communication"),
            Map.entry("communication-emails", "communication"),
            Map.entry("email-templates", "communication"),
            Map.entry("automated-email-settings", "communication"),
            Map.entry("mailings", "communication"),
            Map.entry("text-messages", "communication"),
            Map.entry("text-message-accounts", "communication"),
            Map.entry("messages", "communication"),
            Map.entry("app-notifications", "communication"),
            Map.entry("stored-files", "files"),
            Map.entry("data-imports", "files"),
            Map.entry("data-exports", "files"),
            Map.entry("reports", "reports"),
            Map.entry("analytics-snapshots", "analytics"),
            Map.entry("addons", "addons"),
            Map.entry("org-settings", "settings"),
            Map.entry("subscription-accounts", "subscriptions"),
            Map.entry("public-site-settings", "portals"),
            Map.entry("resident-center-users", "portals"),
            Map.entry("resident-center-contacts", "portals"),
            Map.entry("late-penalty-rules", "late_penalties"),
            Map.entry("meter-readings", "meter_readings"),
            Map.entry("enforcement-cases", "enforcement_cases"),
            Map.entry("debt-inquiry-codes", "debt_inquiry"),
            Map.entry("parcel-deliveries", "parcels"),
            Map.entry("patrol-routes", "patrol_routes"),
            Map.entry("patrol-checkpoints", "patrol_routes"),
            Map.entry("access-control-events", "access_control"),
            Map.entry("staff-timesheets", "timesheets"),
            Map.entry("equipment-assets", "equipment_assets"),
            Map.entry("org-whatsapp-settings", "settings"),
            Map.entry("commercial-lease-terms", "commercial_lease_terms"),
            Map.entry("cooperative-share-records", "cooperative_shares"),
            Map.entry("coop-board-packets", "coop_board_packets")
    );

    public static String resolve(String operationPrefix) {
        if (operationPrefix == null || operationPrefix.isBlank()) {
            return "settings";
        }
        return PREFIX_TO_RESOURCE.getOrDefault(operationPrefix, operationPrefix.replace('-', '_'));
    }
}