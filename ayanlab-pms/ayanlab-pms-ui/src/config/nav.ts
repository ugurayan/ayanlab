import { LEGAL_NAV } from "./legalPages";

export type NavItem = {
  path: string;
  labelKey: string;
  /** Hidden in Apsis lite mode (M0.29) */
  proOnly?: boolean;
};

export type NavSection = {
  id: string;
  labelKey: string;
  items: NavItem[];
};

export const NAV_SECTIONS: NavSection[] = [
  {
    id: "rentals",
    labelKey: "nav.rentals",
    items: [
      { path: "rentals/properties", labelKey: "nav.properties" },
      { path: "rentals/rent-roll", labelKey: "nav.rentRoll" },
      { path: "rentals/tenants", labelKey: "nav.tenants" },
      { path: "rentals/owners", labelKey: "nav.owners" },
      { path: "rentals/balances", labelKey: "nav.balances" }
    ]
  },
  {
    id: "leasing",
    labelKey: "nav.leasing",
    items: [
      { path: "leasing/listings", labelKey: "nav.listings" },
      { path: "leasing/prospects", labelKey: "nav.prospects" },
      { path: "leasing/applications", labelKey: "nav.applications" },
      { path: "leasing/leases", labelKey: "nav.leases" },
      { path: "leasing/commercial", labelKey: "nav.commercial" },
      { path: "leasing/renewals", labelKey: "nav.renewals" }
    ]
  },
  {
    id: "associations",
    labelKey: "nav.associations",
    items: [
      { path: "associations", labelKey: "nav.associationsList" },
      { path: "associations/occupants", labelKey: "nav.occupants" },
      { path: "associations/violations", labelKey: "nav.violations" },
      { path: "associations/architectural", labelKey: "nav.architectural" },
      { path: "associations/assessments", labelKey: "nav.assessments" },
      { path: "associations/cooperative-shares", labelKey: "nav.coopShares" },
      { path: "associations/board-packets", labelKey: "nav.boardPackets" }
    ]
  },
  {
    id: "accounting",
    labelKey: "nav.accounting",
    items: [
      { path: "accounting/coa", labelKey: "nav.coa", proOnly: true },
      { path: "accounting/transactions", labelKey: "nav.transactions" },
      { path: "accounting/bank", labelKey: "nav.bank" },
      { path: "accounting/bills", labelKey: "nav.bills", proOnly: true },
      { path: "accounting/recurring", labelKey: "nav.recurring", proOnly: true },
      { path: "accounting/eft", labelKey: "nav.eft", proOnly: true },
      { path: "accounting/budget", labelKey: "nav.budget" },
      { path: "accounting/company", labelKey: "nav.companyFinancials", proOnly: true }
    ]
  },
  {
    id: "maintenance",
    labelKey: "nav.maintenance",
    items: [
      { path: "maintenance/requests", labelKey: "nav.maintRequests" },
      { path: "maintenance/work-orders", labelKey: "nav.workOrders" },
      { path: "maintenance/vendors", labelKey: "nav.vendors", proOnly: true },
      { path: "maintenance/inspections", labelKey: "nav.inspections", proOnly: true }
    ]
  },
  {
    id: "tasks",
    labelKey: "nav.tasks",
    items: [
      { path: "tasks", labelKey: "nav.tasksList" },
      { path: "tasks/recurring", labelKey: "nav.recurringTasks", proOnly: true }
    ]
  },
  {
    id: "communication",
    labelKey: "nav.communication",
    items: [
      { path: "communication/announcements", labelKey: "nav.announcements" },
      { path: "communication/templates", labelKey: "nav.emailTemplates", proOnly: true },
      { path: "communication/sms", labelKey: "nav.smsLog" },
      { path: "communication/mailing", labelKey: "nav.mailing", proOnly: true },
      { path: "communication/messages", labelKey: "nav.messages" }
    ]
  },
  {
    id: "files",
    labelKey: "nav.files",
    items: [{ path: "files", labelKey: "nav.fileLibrary" }]
  },
  {
    id: "reports",
    labelKey: "nav.reports",
    items: [
      { path: "reports", labelKey: "nav.reportsList" },
      { path: "reports/analytics", labelKey: "nav.analyticsHub", proOnly: true }
    ]
  },
  {
    id: "shortcuts",
    labelKey: "nav.shortcuts",
    items: [{ path: "shortcuts", labelKey: "nav.quickActions", proOnly: true }]
  },
  {
    id: "portals",
    labelKey: "nav.portals",
    items: [
      { path: "portals/resident-center", labelKey: "nav.residentCenter" },
      { path: "portals/tenant", labelKey: "nav.tenantPreview" },
      { path: "portals/owner", labelKey: "nav.ownerPreview" },
      { path: "portals/public-site", labelKey: "nav.publicSite" }
    ]
  },
  {
    id: "tr",
    labelKey: "nav.tr",
    items: [
      { path: "tr/late-penalty", labelKey: "nav.trLatePenalty" },
      { path: "tr/meters", labelKey: "nav.trMeters" },
      { path: "tr/enforcement", labelKey: "nav.trEnforcement" },
      { path: "tr/ats", labelKey: "nav.trAts" },
      { path: "tr/parcels", labelKey: "nav.trParcels" },
      { path: "tr/patrol", labelKey: "nav.trPatrol", proOnly: true },
      { path: "tr/access", labelKey: "nav.trAccess", proOnly: true },
      { path: "tr/timesheets", labelKey: "nav.trTimesheets", proOnly: true },
      { path: "tr/whatsapp", labelKey: "nav.trWhatsapp" }
    ]
  },
  {
    id: "marketplace",
    labelKey: "nav.marketplace",
    items: [
      { path: "marketplace/addons", labelKey: "nav.addons", proOnly: true },
      { path: "marketplace/apps", labelKey: "nav.marketplaceApps", proOnly: true }
    ]
  },
  {
    id: "admin",
    labelKey: "nav.settings",
    items: [
      { path: "settings/profile", labelKey: "nav.profile" },
      { path: "settings/application", labelKey: "nav.application" },
      { path: "settings", labelKey: "nav.orgSettings" },
      { path: "settings/subscriptions", labelKey: "nav.subscriptions", proOnly: true }
    ]
  },
  {
    id: "support-legal",
    labelKey: "nav.supportLegal",
    items: LEGAL_NAV.map(({ slug, labelKey }) => ({
      path: `legal/${slug}`,
      labelKey
    }))
  }
];

export { DEMO_ACCOUNTS, DEMO_LOGIN_ACCOUNTS } from "./demoAccounts";