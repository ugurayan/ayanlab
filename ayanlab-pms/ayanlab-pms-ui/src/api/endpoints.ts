export const endpoints = {
  auth: {
    login: "/auth/api/login",
    me: "/auth/api/me"
  },
  pms: {
    properties: "/properties",
    latePenaltyRules: "/late-penalty-rules",
    meterReadings: "/meter-readings",
    enforcementCases: "/enforcement-cases",
    debtInquiryCodes: "/debt-inquiry-codes",
    parcelDeliveries: "/parcel-deliveries",
    patrolRoutes: "/patrol-routes",
    patrolCheckpoints: "/patrol-checkpoints",
    outstandingBalances: "/outstanding-balances",
    tenants: "/tenants",
    leases: "/leases",
    violations: "/violations",
    architecturalRequests: "/architectural-requests",
    propertyUnits: "/property-units",
    rentalOwners: "/rental-owners",
    maintenanceRequests: "/maintenance-requests",
    accessControlEvents: "/access-control-events",
    staffTimesheets: "/staff-timesheets",
    dashboardWidgets: "/dashboard/pm/widgets"
  }
} as const;