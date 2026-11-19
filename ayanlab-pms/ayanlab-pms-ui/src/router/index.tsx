import { createElement } from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";
import { ProtectedRoute } from "./guards";
import { AppShell } from "../ui/layout/AppShell";
import { LoginPage } from "../views/auth/LoginPage";
import { DashboardPage } from "../views/dashboard/DashboardPage";
import { PropertiesPage } from "../views/rentals/PropertiesPage";
import { PropertyDetailPage } from "../views/rentals/PropertyDetailPage";
import { PropertyCreatePage } from "../views/rentals/PropertyCreatePage";
import { RentRollPage } from "../views/rentals/RentRollPage";
import { TenantsPage } from "../views/rentals/TenantsPage";
import { TenantDetailPage } from "../views/rentals/TenantDetailPage";
import { OwnersPage } from "../views/rentals/OwnersPage";
import { BalancesPage } from "../views/rentals/BalancesPage";
import { LeasesPage } from "../views/leasing/LeasesPage";
import { LeaseEditorPage } from "../views/leasing/LeaseEditorPage";
import { ViolationsPage } from "../views/associations/ViolationsPage";
import { ViolationDetailPage } from "../views/associations/ViolationDetailPage";
import { ArchitecturalPage } from "../views/associations/ArchitecturalPage";
import { ArchitecturalDetailPage } from "../views/associations/ArchitecturalDetailPage";
import { ResidentCenterPage } from "../views/portals/ResidentCenterPage";
import { TenantPortalPage } from "../views/portals/TenantPortalPage";
import { OwnerPortalPage } from "../views/portals/OwnerPortalPage";
import {
  BankAccountsPage,
  BillsPage,
  BudgetPage,
  ChartOfAccountsPage,
  CompanyFinancialsPage,
  EftApprovalsPage,
  RecurringPage,
  TransactionsPage
} from "../views/accounting/AccountingPages";
import { AtsPage, EnforcementPage, LatePenaltyPage, MetersPage } from "../views/tr/TrPages";
import { MOCK_ROUTE_PAGES } from "../views/mock-modules/allPages";
import { NotFoundPage } from "../views/NotFoundPage";
import { NAV_SECTIONS } from "../config/nav";
import { ProfilePage } from "../views/settings/ProfilePage";
import { ApplicationSettingsPage } from "../views/settings/ApplicationSettingsPage";
import { LegalDocPage } from "../views/legal/LegalDocPage";

const mockModuleRoutes = NAV_SECTIONS.flatMap((s) => s.items)
  .map((item) => item.path)
  .filter((path) => MOCK_ROUTE_PAGES[path])
  .map((path) => ({
    path,
    element: createElement(MOCK_ROUTE_PAGES[path])
  }));

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  { path: "/legal/:slug", element: <LegalDocPage /> },
  {
    path: "/app",
    element: (
      <ProtectedRoute>
        <AppShell />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/app/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "rentals/properties", element: <PropertiesPage /> },
      { path: "rentals/properties/new", element: <PropertyCreatePage /> },
      { path: "rentals/properties/:id", element: <PropertyDetailPage /> },
      { path: "rentals/rent-roll", element: <RentRollPage /> },
      { path: "rentals/tenants", element: <TenantsPage /> },
      { path: "rentals/tenants/:id", element: <TenantDetailPage /> },
      { path: "rentals/owners", element: <OwnersPage /> },
      { path: "rentals/balances", element: <BalancesPage /> },
      { path: "leasing/leases", element: <LeasesPage /> },
      { path: "leasing/leases/new", element: <LeaseEditorPage /> },
      { path: "leasing/leases/:id", element: <LeaseEditorPage /> },
      { path: "associations/violations", element: <ViolationsPage /> },
      { path: "associations/violations/:id", element: <ViolationDetailPage /> },
      { path: "associations/architectural", element: <ArchitecturalPage /> },
      { path: "associations/architectural/:id", element: <ArchitecturalDetailPage /> },
      { path: "accounting/coa", element: <ChartOfAccountsPage /> },
      { path: "accounting/transactions", element: <TransactionsPage /> },
      { path: "accounting/bank", element: <BankAccountsPage /> },
      { path: "accounting/bills", element: <BillsPage /> },
      { path: "accounting/recurring", element: <RecurringPage /> },
      { path: "accounting/eft", element: <EftApprovalsPage /> },
      { path: "accounting/budget", element: <BudgetPage /> },
      { path: "accounting/company", element: <CompanyFinancialsPage /> },
      { path: "tr/late-penalty", element: <LatePenaltyPage /> },
      { path: "tr/meters", element: <MetersPage /> },
      { path: "tr/enforcement", element: <EnforcementPage /> },
      { path: "tr/ats", element: <AtsPage /> },
      { path: "portals/resident-center", element: <ResidentCenterPage /> },
      { path: "portals/tenant", element: <TenantPortalPage /> },
      { path: "portals/owner", element: <OwnerPortalPage /> },
      { path: "settings/profile", element: <ProfilePage /> },
      { path: "settings/application", element: <ApplicationSettingsPage /> },
      { path: "legal/:slug", element: <LegalDocPage /> },
      { path: "help", element: <Navigate to="/app/legal/help-faq" replace /> },
      ...mockModuleRoutes
    ]
  },
  { path: "/", element: <Navigate to="/app" replace /> },
  { path: "*", element: <NotFoundPage /> }
]);